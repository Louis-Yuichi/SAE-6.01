package app.metier;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class VRPSolution
{
	private List<List<Integer>> listeTournees;
	private double              coutTotal;
	private VRPData             donneesVRP;

	public VRPSolution(VRPData donneesVRP, List<List<Integer>> listeTournees)
	{
		this.donneesVRP     = donneesVRP;
		this.listeTournees  = listeTournees;
		this.coutTotal      = this.calculerCoutTotal();
	}

	public static VRPSolution genererSolutionInitiale(VRPData donneesVRP, int nombreMaxVehicules, Random generateur)
	{
		List<Integer> listeClients = new ArrayList<>();

		for (int cpt = 1; cpt <= donneesVRP.getNombreClients(); cpt++)
		{
			listeClients.add(cpt);
		}

		Collections.shuffle(listeClients, generateur);

		List<List<Integer>> listeTournees  = new ArrayList<>();
		List<Integer>       tourneeEnCours = new ArrayList<>();

		int chargeActuelle = 0;
		for (int client : listeClients)
		{
			if (listeTournees.size() < nombreMaxVehicules - 1 && chargeActuelle + donneesVRP.getDemande(client) > donneesVRP.getCapacite())
			{
				if (!tourneeEnCours.isEmpty())
				{
					listeTournees.add(tourneeEnCours);
				}

				tourneeEnCours = new ArrayList<>();
				chargeActuelle = 0;
			}

			tourneeEnCours.add(client);
			chargeActuelle += donneesVRP.getDemande(client);
		}

		if (!tourneeEnCours.isEmpty())
		{
			listeTournees.add(tourneeEnCours);
		}

		return new VRPSolution(donneesVRP, listeTournees);
	}

	public VRPSolution copierSolution()
	{
		List<List<Integer>> copieTournees = new ArrayList<>();

		for (List<Integer> tournee : this.listeTournees)
		{
			copieTournees.add(new ArrayList<>(tournee));
		}

		return new VRPSolution(this.donneesVRP, copieTournees);
	}

	public VRPSolution genererVoisin(Random generateur)
	{
		switch (generateur.nextInt(3))
		{
			case 0:  return this.genererVoisinParechange(generateur);
			case 1:  return this.genererVoisinParDeplacement(generateur);
			default: return this.genererVoisinPar2Opt(generateur);
		}
	}

	private VRPSolution genererVoisinParechange(Random generateur)
	{
		VRPSolution solutionVoisine = this.copierSolution();
		int nombreTournees = solutionVoisine.listeTournees.size();

		if (nombreTournees == 0)
		{
			return null;
		}

		boolean echangeIntraTournee = nombreTournees < 2 || generateur.nextBoolean();

		if (echangeIntraTournee)
		{
			List<Integer> tournee = solutionVoisine.listeTournees.get(generateur.nextInt(nombreTournees));

			if (tournee.size() < 2)
			{
				return null;
			}

			int indiceClient1 = generateur.nextInt(tournee.size());
			int indiceClient2;

			do
			{
				indiceClient2 = generateur.nextInt(tournee.size());
			}
			while (indiceClient2 == indiceClient1);

			Collections.swap(tournee, indiceClient1, indiceClient2);
		}
		else
		{
			int indiceTournee1 = generateur.nextInt(nombreTournees);
			int indiceTournee2;

			do
			{
				indiceTournee2 = generateur.nextInt(nombreTournees);
			}
			while (indiceTournee2 == indiceTournee1);

			List<Integer> tournee1 = solutionVoisine.listeTournees.get(indiceTournee1);
			List<Integer> tournee2 = solutionVoisine.listeTournees.get(indiceTournee2);

			if (tournee1.isEmpty() || tournee2.isEmpty())
			{
				return null;
			}

			int indiceClient1 = generateur.nextInt(tournee1.size());
			int indiceClient2 = generateur.nextInt(tournee2.size());
			int client1 = tournee1.get(indiceClient1);
			int client2 = tournee2.get(indiceClient2);

			if (this.calculerCharge(tournee1) - this.donneesVRP.getDemande(client1) + this.donneesVRP.getDemande(client2) > this.donneesVRP.getCapacite())
			{
				return null;
			}

			if (this.calculerCharge(tournee2) - this.donneesVRP.getDemande(client2) + this.donneesVRP.getDemande(client1) > this.donneesVRP.getCapacite())
			{
				return null;
			}

			tournee1.set(indiceClient1, client2);
			tournee2.set(indiceClient2, client1);
		}

		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	private VRPSolution genererVoisinParDeplacement(Random generateur)
	{
		VRPSolution solutionVoisine = this.copierSolution();
		int nombreTournees = solutionVoisine.listeTournees.size();

		if (nombreTournees < 2)
		{
			return null;
		}

		int indiceTourneeSource     = generateur.nextInt(nombreTournees);
		List<Integer> tourneeSource = solutionVoisine.listeTournees.get(indiceTourneeSource);

		if (tourneeSource.size() <= 1)
		{
			return null;
		}

		int indiceTourneeDestination;

		do
		{
			indiceTourneeDestination = generateur.nextInt(nombreTournees);
		}
		while (indiceTourneeDestination == indiceTourneeSource);

		List<Integer> tourneeDestination = solutionVoisine.listeTournees.get(indiceTourneeDestination);

		int indiceClient    = generateur.nextInt(tourneeSource.size());
		int clientÀDeplacer = tourneeSource.get(indiceClient);

		if (this.calculerCharge(tourneeDestination) + this.donneesVRP.getDemande(clientÀDeplacer) > this.donneesVRP.getCapacite())
		{
			return null;
		}

		tourneeSource.remove(indiceClient);
		tourneeDestination.add(generateur.nextInt(tourneeDestination.size() + 1), clientÀDeplacer);
		solutionVoisine.listeTournees.removeIf(List::isEmpty);
		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	private VRPSolution genererVoisinPar2Opt(Random generateur)
	{
		VRPSolution solutionVoisine = this.copierSolution();

		if (solutionVoisine.listeTournees.isEmpty())
		{
			return null;
		}

		List<Integer> tournee = solutionVoisine.listeTournees.get(generateur.nextInt(solutionVoisine.listeTournees.size()));

		if (tournee.size() < 3)
		{
			return null;
		}

		int indiceDebut = generateur.nextInt(tournee.size() - 1);
		int indiceFin = indiceDebut + 1 + generateur.nextInt(tournee.size() - indiceDebut - 1);

		Collections.reverse(tournee.subList(indiceDebut, indiceFin + 1));
		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	private int calculerCharge(List<Integer> tournee)
	{
		int chargeTotal = 0;

		for (int client : tournee)
		{
			chargeTotal += this.donneesVRP.getDemande(client);
		}

		return chargeTotal;
	}

	private double calculerCoutTotal()
	{
		double coutTotal = 0;
		
		for (List<Integer> tournee : this.listeTournees)
		{
			if (tournee.isEmpty())
			{
				continue;
			}

			coutTotal += this.donneesVRP.obtenirDistance(0, tournee.get(0));

			for (int cpt = 0; cpt < tournee.size() - 1; cpt++)
			{
				coutTotal += this.donneesVRP.obtenirDistance(tournee.get(cpt), tournee.get(cpt + 1));
			}

			coutTotal += this.donneesVRP.obtenirDistance(tournee.get(tournee.size() - 1), 0);
		}
		
		return coutTotal;
	}

	public double              getCoutTotal()      { return this.coutTotal;            }
	public List<List<Integer>> getTournees()       { return this.listeTournees;        }
	public VRPData             getDonneesVRP()     { return this.donneesVRP;           }
	public int                 getNombreTournees() { return this.listeTournees.size(); }

	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("Coût : %.2f | %d tournees\n", this.coutTotal, this.listeTournees.size()));
		
		for (int cpt = 0; cpt < this.listeTournees.size(); cpt++)
		{
			int numeroVehicule = cpt + 1;
			List<Integer> tournee = this.listeTournees.get(cpt);
			int chargeActuelle = this.calculerCharge(tournee);
			int capaciteMaximale = this.donneesVRP.getCapacite();

			sb.append(String.format("  V%d : Depôt → %s → Depôt  (capacite : %d/%d)\n",
													numeroVehicule, tournee, chargeActuelle, capaciteMaximale));
		}

		return sb.toString();
	}
}