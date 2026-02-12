package app.metier;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class VRPSolution
{
	private List<List<Integer>> tournees;

	private double  coutTotal;
	private VRPData data;

	public VRPSolution(VRPData data, List<List<Integer>> tournees)
	{
		this.data      = data;
		this.tournees  = tournees;
		this.coutTotal = calculerCoutTotal();
	}

	/** Solution initiale aléatoire respectant les capacités. */
	public static VRPSolution générerSolutionInitiale(VRPData donnéesVRP, int nombreMaxVéhicules, Random générateur)
	{
		List<Integer> listeClients = new ArrayList<>();

		for (int cpt = 1; cpt <= donnéesVRP.getNombreClients(); cpt++)
			listeClients.add(cpt);

		Collections.shuffle(listeClients, générateur);

		List<List<Integer>> tournées = new ArrayList<>();
		List<Integer>       tournéeEnCours = new ArrayList<>();

		int chargeActuelle = 0;

		for (int client : listeClients)
		{
			if (tournées.size() < nombreMaxVéhicules - 1 && chargeActuelle + donnéesVRP.getDemande(client) > donnéesVRP.getCapacité())
			{
				if (!tournéeEnCours.isEmpty())
					tournées.add(tournéeEnCours);

				tournéeEnCours = new ArrayList<>(); chargeActuelle = 0;
			}

			tournéeEnCours.add(client); chargeActuelle += donnéesVRP.getDemande(client);
		}

		if (!tournéeEnCours.isEmpty())
			tournées.add(tournéeEnCours);

		return new VRPSolution(donnéesVRP, tournées);
	}

	public VRPSolution copierSolution()
	{
		List<List<Integer>> copieTournées = new ArrayList<>();

		for (List<Integer> tournée : tournees)
			copieTournées.add(new ArrayList<>(tournée));

		return new VRPSolution(data, copieTournées);
	}

	/** Génère un voisin aléatoire (swap/relocate/2-opt). Retourne null si infaisable. */
	public VRPSolution générerVoisin(Random générateur)
	{
		switch (générateur.nextInt(3))
		{
			case 0:  return générerVoisinParÉchange(générateur);
			case 1:  return générerVoisinParDéplacement(générateur);
			default: return générerVoisinPar2Opt(générateur);
		}
	}

	private VRPSolution générerVoisinParÉchange(Random générateur)
	{
		VRPSolution solutionVoisine = copierSolution();

		int nombreTournées = solutionVoisine.tournees.size();

		if (nombreTournées == 0)
			return null;

		boolean échangeIntraTournée = nombreTournées < 2 || générateur.nextBoolean();

		if (échangeIntraTournée)
		{
			List<Integer> tournée = solutionVoisine.tournees.get(générateur.nextInt(nombreTournées));

			if (tournée.size() < 2)
				return null;

			int indiceClient1 = générateur.nextInt(tournée.size()), indiceClient2;

			do
			{
				indiceClient2 = générateur.nextInt(tournée.size());
			} while (indiceClient2 == indiceClient1);

			Collections.swap(tournée, indiceClient1, indiceClient2);
		}
		else
		{
			int indiceTournée1 = générateur.nextInt(nombreTournées), indiceTournée2;

			do
			{
				indiceTournée2 = générateur.nextInt(nombreTournées);
			} while (indiceTournée2 == indiceTournée1);

			List<Integer> tournée1 = solutionVoisine.tournees.get(indiceTournée1), tournée2 = solutionVoisine.tournees.get(indiceTournée2);

			if (tournée1.isEmpty() || tournée2.isEmpty())
				return null;

			int indiceClient1 = générateur.nextInt(tournée1.size());
			int indiceClient2 = générateur.nextInt(tournée2.size());

			int client1 = tournée1.get(indiceClient1), client2 = tournée2.get(indiceClient2);

			if (calculerCharge(tournée1) - data.getDemande(client1) + data.getDemande(client2) > data.getCapacité())
				return null;

			if (calculerCharge(tournée2) - data.getDemande(client2) + data.getDemande(client1) > data.getCapacité())
				return null;

			tournée1.set(indiceClient1, client2); tournée2.set(indiceClient2, client1);
		}

		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	private VRPSolution générerVoisinParDéplacement(Random générateur)
	{
		VRPSolution solutionVoisine = copierSolution();

		int nombreTournées = solutionVoisine.tournees.size();

		if (nombreTournées < 2)
			return null;

		int indiceTournéeSource = générateur.nextInt(nombreTournées);
		List<Integer> tournéeSource = solutionVoisine.tournees.get(indiceTournéeSource);

		if (tournéeSource.size() <= 1)
			return null;

		int indiceTournéeDestination;

		do
		{
			indiceTournéeDestination = générateur.nextInt(nombreTournées);
		} while (indiceTournéeDestination == indiceTournéeSource);

		List<Integer> tournéeDestination = solutionVoisine.tournees.get(indiceTournéeDestination);

		int indiceClient = générateur.nextInt(tournéeSource.size());
		int clientÀDéplacer = tournéeSource.get(indiceClient);

		if (calculerCharge(tournéeDestination) + data.getDemande(clientÀDéplacer) > data.getCapacité())
			return null;

		tournéeSource.remove(indiceClient);
		tournéeDestination.add(générateur.nextInt(tournéeDestination.size() + 1), clientÀDéplacer);

		solutionVoisine.tournees.removeIf(List::isEmpty);

		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	private VRPSolution générerVoisinPar2Opt(Random générateur)
	{
		VRPSolution solutionVoisine = copierSolution();
		
		if (solutionVoisine.tournees.isEmpty())
			return null;
		
		List<Integer> tournée = solutionVoisine.tournees.get(générateur.nextInt(solutionVoisine.tournees.size()));
		
		if (tournée.size() < 3)
			return null;
		
		int indiceDebut = générateur.nextInt(tournée.size() - 1);
		int indiceFin = indiceDebut + 1 + générateur.nextInt(tournée.size() - indiceDebut - 1);
		
		Collections.reverse(tournée.subList(indiceDebut, indiceFin + 1));
		
		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();
		
		return solutionVoisine;
	}

	private int calculerCharge(List<Integer> tournée)
	{
		int chargeTotal = 0;
		
		for (int client : tournée)
			chargeTotal += data.getDemande(client);
		
		return chargeTotal;
	}

	private double calculerCoutTotal()
	{
		double coutTotal = 0;
		
		for (List<Integer> tournée : tournees)
		{
			if (tournée.isEmpty())
				continue;
			
			coutTotal += data.obtenirDistance(0, tournée.get(0));
			
			for (int cpt = 0; cpt < tournée.size() - 1; cpt++)
				coutTotal += data.obtenirDistance(tournée.get(cpt), tournée.get(cpt + 1));
			
			coutTotal += data.obtenirDistance(tournée.get(tournée.size() - 1), 0);
		}
		
		return coutTotal;
	}

	public double              getCoutTotal()        { return coutTotal;       }
	public List<List<Integer>> getTournées()      { return tournees;        }
	public VRPData             getDonnéesVRP()    { return data;            }
	public int                 getNombreTournées() { return tournees.size(); }

	@Override
	public String toString()
	{
		StringBuilder constructeurTexte = new StringBuilder();
		
		constructeurTexte.append(String.format("Coût : %.2f | %d tournées\n", coutTotal, tournees.size()));
		
		for (int cpt = 0; cpt < tournees.size(); cpt++)
		{
			int numéroVéhicule = cpt + 1;
			List<Integer> tournée = tournees.get(cpt);
			int chargeActuelle = calculerCharge(tournée);
			int capacitéMaximale = data.getCapacité();
			
			constructeurTexte.append(String.format("  V%d : Dépôt → %s → Dépôt  (capacité : %d/%d)\n",
				numéroVéhicule, tournée, chargeActuelle, capacitéMaximale));
		}
		
		return constructeurTexte.toString();
	}
}
