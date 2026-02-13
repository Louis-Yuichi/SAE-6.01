package app.metier;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Représente une solution au problème VRP (Vehicle Routing Problem).
 * Contient une liste de tournées et calcule le coût total associé.
 */
public class VRPSolution
{
	private List<List<Integer>> listeTournees; // Liste des tournées, chaque tournée contient les IDs des clients
	private double              coutTotal;     // Coût total de la solution (distance totale parcourue)
	private VRPData             donneesVRP;    // Données du problème VRP

	/**
	 * Construit une solution VRP avec les tournées données.
	 * @param donneesVRP Données du problème (distances, demandes, capacités)
	 * @param listeTournees Liste des tournées (chaque tournée = liste d'IDs clients)
	 */
	public VRPSolution(VRPData donneesVRP, List<List<Integer>> listeTournees)
	{
		this.donneesVRP     = donneesVRP;
		this.listeTournees  = listeTournees;
		this.coutTotal      = this.calculerCoutTotal();
	}

	/**
	 * Génère une solution initiale aléatoire en distribuant les clients dans des tournées.
	 * Respecte la contrainte de capacité des véhicules.
	 * @param donneesVRP Données du problème VRP
	 * @param nombreMaxVehicules Nombre maximum de véhicules autorisés
	 * @param generateur Générateur de nombres aléatoires
	 * @return Une solution initiale valide
	 */
	public static VRPSolution genererSolutionInitiale(VRPData donneesVRP, int nombreMaxVehicules, Random generateur)
	{
		// Créer une liste de tous les clients
		List<Integer> listeClients = new ArrayList<>();

		for (int cpt = 1; cpt <= donneesVRP.getNombreClients(); cpt++)
		{
			listeClients.add(cpt);
		}

		// Mélanger aléatoirement les clients
		Collections.shuffle(listeClients, generateur);

		List<List<Integer>> listeTournees  = new ArrayList<>();
		List<Integer>       tourneeEnCours = new ArrayList<>();

		int chargeActuelle = 0;
		
		// Distribuer les clients dans des tournées en respectant la capacité
		for (int client : listeClients)
		{
			// Si ajout du client dépasse la capacité et on n'a pas atteint le max de véhicules
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

		// Ajouter la dernière tournée si non vide
		if (!tourneeEnCours.isEmpty())
		{
			listeTournees.add(tourneeEnCours);
		}

		return new VRPSolution(donneesVRP, listeTournees);
	}

	/**
	 * Crée une copie profonde de cette solution.
	 * @return Une nouvelle instance de VRPSolution identique
	 */
	public VRPSolution copierSolution()
	{
		List<List<Integer>> copieTournees = new ArrayList<>();

		for (List<Integer> tournee : this.listeTournees)
		{
			copieTournees.add(new ArrayList<>(tournee));
		}

		return new VRPSolution(this.donneesVRP, copieTournees);
	}

	/**
	 * Génère une solution voisine en appliquant aléatoirement un opérateur de transformation.
	 * Trois opérateurs possibles : échange, déplacement, 2-opt.
	 * @param generateur Générateur de nombres aléatoires
	 * @return Une solution voisine, ou null si impossible de générer un voisin
	 */
	public VRPSolution genererVoisin(Random generateur)
	{
		switch (generateur.nextInt(3))
		{
			case 0:  return this.genererVoisinParechange(generateur);
			case 1:  return this.genererVoisinParDeplacement(generateur);
			default: return this.genererVoisinPar2Opt(generateur);
		}
	}

	/**
	 * Génère un voisin par échange de deux clients (intra ou inter-tournée).
	 * - Échange intra-tournée : échange deux clients dans la même tournée
	 * - Échange inter-tournée : échange deux clients entre deux tournées différentes
	 * Vérifie les contraintes de capacité pour l'échange inter-tournée.
	 * @param generateur Générateur de nombres aléatoires
	 * @return Une solution voisine, ou null si impossible
	 */
	private VRPSolution genererVoisinParechange(Random generateur)
	{
		VRPSolution solutionVoisine = this.copierSolution();
		int nombreTournees = solutionVoisine.listeTournees.size();

		if (nombreTournees == 0)
		{
			return null;
		}

		// Décider si échange intra ou inter-tournée
		boolean echangeIntraTournee = nombreTournees < 2 || generateur.nextBoolean();

		if (echangeIntraTournee)
		{
			// Échange intra-tournée : swap deux clients dans la même tournée
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
			// Échange inter-tournée : swap deux clients entre deux tournées différentes
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

			// Vérifier que l'échange respecte les contraintes de capacité
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

	/**
	 * Génère un voisin par déplacement d'un client d'une tournée vers une autre.
	 * Vérifie que la tournée de destination ne dépasse pas sa capacité.
	 * Supprime les tournées vides après le déplacement.
	 * @param generateur Générateur de nombres aléatoires
	 * @return Une solution voisine, ou null si impossible
	 */
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

		// Choisir une tournée de destination différente
		int indiceTourneeDestination;

		do
		{
			indiceTourneeDestination = generateur.nextInt(nombreTournees);
		}
		while (indiceTourneeDestination == indiceTourneeSource);

		List<Integer> tourneeDestination = solutionVoisine.listeTournees.get(indiceTourneeDestination);

		int indiceClient    = generateur.nextInt(tourneeSource.size());
		int clientÀDeplacer = tourneeSource.get(indiceClient);

		// Vérifier que la tournée de destination peut accueillir le client
		if (this.calculerCharge(tourneeDestination) + this.donneesVRP.getDemande(clientÀDeplacer) > this.donneesVRP.getCapacite())
		{
			return null;
		}

		// Déplacer le client
		tourneeSource.remove(indiceClient);
		tourneeDestination.add(generateur.nextInt(tourneeDestination.size() + 1), clientÀDeplacer);
		
		// Supprimer les tournées vides
		solutionVoisine.listeTournees.removeIf(List::isEmpty);
		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	/**
	 * Génère un voisin par 2-opt : inverse l'ordre d'un segment de clients dans une tournée.
	 * Améliore la séquence de visite des clients sans changer l'affectation aux tournées.
	 * @param generateur Générateur de nombres aléatoires
	 * @return Une solution voisine, ou null si impossible
	 */
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

		// Choisir deux positions aléatoires et inverser le segment entre elles
		int indiceDebut = generateur.nextInt(tournee.size() - 1);
		int indiceFin = indiceDebut + 1 + generateur.nextInt(tournee.size() - indiceDebut - 1);

		Collections.reverse(tournee.subList(indiceDebut, indiceFin + 1));
		solutionVoisine.coutTotal = solutionVoisine.calculerCoutTotal();

		return solutionVoisine;
	}

	/**
	 * Calcule la charge totale (somme des demandes) d'une tournée.
	 * @param tournee La tournée dont on veut calculer la charge
	 * @return La charge totale de la tournée
	 */
	private int calculerCharge(List<Integer> tournee)
	{
		int chargeTotal = 0;

		for (int client : tournee)
		{
			chargeTotal += this.donneesVRP.getDemande(client);
		}

		return chargeTotal;
	}

	/**
	 * Calcule le coût total de la solution (distance totale parcourue par tous les véhicules).
	 * Pour chaque tournée : dépôt → client1 → client2 → ... → clientN → dépôt.
	 * @return Le coût total (somme des distances)
	 */
	private double calculerCoutTotal()
	{
		double coutTotal = 0;
		
		for (List<Integer> tournee : this.listeTournees)
		{
			if (tournee.isEmpty())
			{
				continue;
			}

			// Distance dépôt → premier client
			coutTotal += this.donneesVRP.obtenirDistance(0, tournee.get(0));

			// Distances entre clients consécutifs
			for (int cpt = 0; cpt < tournee.size() - 1; cpt++)
			{
				coutTotal += this.donneesVRP.obtenirDistance(tournee.get(cpt), tournee.get(cpt + 1));
			}

			// Distance dernier client → dépôt
			coutTotal += this.donneesVRP.obtenirDistance(tournee.get(tournee.size() - 1), 0);
		}
		
		return coutTotal;
	}

	// Accesseurs
	public double              getCoutTotal()      { return this.coutTotal;            }
	public List<List<Integer>> getTournees()       { return this.listeTournees;        }
	public VRPData             getDonneesVRP()     { return this.donneesVRP;           }
	public int                 getNombreTournees() { return this.listeTournees.size(); }

	/**
	 * Représentation textuelle de la solution.
	 * Affiche le coût, le nombre de tournées et le détail de chaque tournée avec sa charge.
	 * @return Une chaîne décrivant la solution
	 */
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