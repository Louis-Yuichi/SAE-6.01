package app.metier;

import java.util.*;

/** Solution VRP = liste de tournées. Voisinages : swap (intra/inter), relocate, 2-opt. */
public class VRPSolution
{
	private List<List<Integer>> tournees;
	private double coutTotal;
	private VRPData data;

	public VRPSolution(VRPData data, List<List<Integer>> tournees)
	{
		this.data = data; this.tournees = tournees; this.coutTotal = calculerCout();
	}

	/** Solution initiale aléatoire respectant les capacités. */
	public static VRPSolution genererAleatoire(VRPData data, int maxVeh, Random rng)
	{
		List<Integer> clients = new ArrayList<>();

		for (int c = 1; c <= data.getNbClients(); c++)
			clients.add(c);

		Collections.shuffle(clients, rng);

		List<List<Integer>> t = new ArrayList<>();
		List<Integer>       route = new ArrayList<>();

		int charge = 0;

		for (int c : clients)
		{
			if (t.size() < maxVeh - 1 && charge + data.getDemande(c) > data.getCapacite())
			{
				if (!route.isEmpty())
					t.add(route);

				route = new ArrayList<>(); charge = 0;
			}

			route.add(c); charge += data.getDemande(c);
		}

		if (!route.isEmpty())
			t.add(route);

		return new VRPSolution(data, t);
	}

	public VRPSolution copier()
	{
		List<List<Integer>> c = new ArrayList<>();

		for (List<Integer> t : tournees)
			c.add(new ArrayList<>(t));

		return new VRPSolution(data, c);
	}

	/** Génère un voisin aléatoire (swap/relocate/2-opt). Retourne null si infaisable. */
	public VRPSolution voisin(Random rng)
	{
		switch (rng.nextInt(3))
		{
			case 0:  return voisinSwap(rng);
			case 1:  return voisinRelocate(rng);
			default: return voisin2opt(rng);
		}
	}

	private VRPSolution voisinSwap(Random rng)
	{
		VRPSolution v = copier();

		int nbT = v.tournees.size();

		if (nbT == 0)
			return null;

		boolean intra = nbT < 2 || rng.nextBoolean();

		if (intra)
		{
			List<Integer> r = v.tournees.get(rng.nextInt(nbT));

			if (r.size() < 2)
				return null;

			int i = rng.nextInt(r.size()), j;

			do
			{
				j = rng.nextInt(r.size());
			} while (j == i);

			Collections.swap(r, i, j);
		}
		else
		{
			int t1 = rng.nextInt(nbT), t2;

			do
			{
				t2 = rng.nextInt(nbT);
			} while (t2 == t1);

			List<Integer> r1 = v.tournees.get(t1), r2 = v.tournees.get(t2);

			if (r1.isEmpty() || r2.isEmpty())
				return null;

			int i = rng.nextInt(r1.size());
			int j = rng.nextInt(r2.size());

			int a = r1.get(i), b = r2.get(j);

			if (charge(r1) - data.getDemande(a) + data.getDemande(b) > data.getCapacite())
				return null;

			if (charge(r2) - data.getDemande(b) + data.getDemande(a) > data.getCapacite())
				return null;

			r1.set(i, b); r2.set(j, a);
		}

		v.coutTotal = v.calculerCout();

		return v;
	}

	private VRPSolution voisinRelocate(Random rng)
	{
		VRPSolution v = copier();

		int nbT = v.tournees.size();

		if (nbT < 2)
			return null;

		int ts = rng.nextInt(nbT);
		List<Integer> src = v.tournees.get(ts);

		if (src.size() <= 1)
			return null;

		int td;

		do
		{
			td = rng.nextInt(nbT);
		} while (td == ts);

		List<Integer> dst = v.tournees.get(td);

		int idx = rng.nextInt(src.size());
		int cli = src.get(idx);

		if (charge(dst) + data.getDemande(cli) > data.getCapacite())
			return null;

		src.remove(idx);
		dst.add(rng.nextInt(dst.size() + 1), cli);

		v.tournees.removeIf(List::isEmpty);

		v.coutTotal = v.calculerCout();

		return v;
	}

	private VRPSolution voisin2opt(Random rng)
	{
		VRPSolution v = copier();
		
		if (v.tournees.isEmpty())
			return null;
		
		List<Integer> r = v.tournees.get(rng.nextInt(v.tournees.size()));
		
		if (r.size() < 3)
			return null;
		
		int i = rng.nextInt(r.size() - 1);
		int j = i + 1 + rng.nextInt(r.size() - i - 1);
		
		Collections.reverse(r.subList(i, j + 1));
		
		v.coutTotal = v.calculerCout();
		
		return v;
	}

	private int charge(List<Integer> r)
	{
		int c = 0;
		
		for (int x : r)
			c += data.getDemande(x);
		
		return c;
	}

	private double calculerCout()
	{
		double tot = 0;
		
		for (List<Integer> r : tournees)
		{
			if (r.isEmpty())
				continue;
			
			tot += data.dist(0, r.get(0));
			
			for (int i = 0; i < r.size() - 1; i++)
				tot += data.dist(r.get(i), r.get(i + 1));
			
			tot += data.dist(r.get(r.size() - 1), 0);
		}
		
		return tot;
	}

	public double              getCout()       { return coutTotal;       }
	public List<List<Integer>> getTournees()   { return tournees;        }
	public VRPData             getData()       { return data;            }
	public int                 getNbTournees() { return tournees.size(); }

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Coût : %.2f | %d tournées\n", coutTotal, tournees.size()));
		
		for (int i = 0; i < tournees.size(); i++)
		{
			int vehicleNumber = i + 1;
			List<Integer> tournee = tournees.get(i);
			int chargeActuelle = charge(tournee);
			int capaciteMax = data.getCapacite();
			
			sb.append(String.format("  V%d : Dépôt → %s → Dépôt  (%d/%d)\n",
				vehicleNumber, tournee, chargeActuelle, capaciteMax));
		}
		
		return sb.toString();
	}
}
