package app.metier;

import java.util.Random;

/** Recuit simulé pour VRP. Deux modes : voisin aléatoire ou meilleur parmi 2n. */
public class RecuitSimule
{
	public interface Callback
	{
		void surItération(int numéroItération, VRPSolution solutionCourante, VRPSolution meilleureSolution, double température);
		void surTerminaison(VRPSolution meilleureSolution, long duréeMillisecondes);
	}

	private          VRPData  donnéesVRP;
	private          double   températureInitiale, températureMinimale, coefficientRefroidissement;
	private          int      nombreItérationsPalier, nombreMaxItérationsSansAmélioration, nombreMaxVéhicules;
	private          boolean  choisirMeilleurVoisin;
	private          Callback fonctionCallback;
	private volatile boolean estArrêté;

	public RecuitSimule( VRPData donnéesVRP, double températureInitiale, double températureMinimale, double coefficientRefroidissement, int nombreItérationsPalier, int nombreMaxItérationsSansAmélioration, int nombreMaxVéhicules, boolean choisirMeilleurVoisin)
	{
		this.donnéesVRP = donnéesVRP; this.températureInitiale = températureInitiale; this.températureMinimale = températureMinimale; this.coefficientRefroidissement = coefficientRefroidissement;
		this.nombreItérationsPalier = nombreItérationsPalier; this.nombreMaxItérationsSansAmélioration = nombreMaxItérationsSansAmélioration;
		this.nombreMaxVéhicules = nombreMaxVéhicules; this.choisirMeilleurVoisin = choisirMeilleurVoisin;
	}

	public void définirCallback(Callback callback) { this.fonctionCallback = callback  ; }
	public void arrêter()                        { this.estArrêté       = true; }

	/**
	 * Exécute l'algorithme de recuit simulé.
	 * @return La meilleure solution trouvée
	 */
	public VRPSolution exécuter()
	{
		long   instantDébut = System.currentTimeMillis();
		Random générateurAléatoire   = new Random();

		// 1. Solution initiale aléatoire
		VRPSolution solutionCourante  = VRPSolution.générerSolutionInitiale(donnéesVRP, nombreMaxVéhicules, générateurAléatoire);
		VRPSolution meilleureSolution = solutionCourante.copierSolution();

		// 2. Paramètres du recuit
		double température       = températureInitiale;                      // Température initiale
		int    compteurSansAmélioration = 0;                       // Compteur sans amélioration
		int    compteurItérations       = 0;                       // Compteur d'itérations
		int    nombreMaxTentatives    = 2 * donnéesVRP.getNombreClients(); // Taille max du voisinage (2n)

		// 3. Boucle principale : tant que T > Tmin et pas trop longtemps sans amélioration
		while (température > températureMinimale && compteurSansAmélioration < nombreMaxItérationsSansAmélioration && !estArrêté)
		{
			boolean améliorationTrouvée = false;

			// Palier : nombre d'itérations à température constante
			for (int cptPalier = 0; cptPalier < nombreItérationsPalier && !estArrêté; cptPalier++)
			{
				// Chercher un voisin (aléatoire ou meilleur parmi 2n)
				VRPSolution solutionVoisine = choisirVoisin(solutionCourante, générateurAléatoire, nombreMaxTentatives);

				// Si aucun voisin faisable trouvé, passer à l'itération suivante
				if (solutionVoisine == null)
					continue;

				// Critère de Metropolis : accepter si meilleur OU avec probabilité exp(-ΔE/T)
				double différenceCoût = solutionVoisine.getCoutTotal() - solutionCourante.getCoutTotal();

				if (différenceCoût <= 0 || générateurAléatoire.nextDouble() < Math.exp(-différenceCoût / température))
				{
					solutionCourante = solutionVoisine; // Accepter la solution voisine

					// Si nouveau meilleur global, sauvegarder
					if (solutionCourante.getCoutTotal() < meilleureSolution.getCoutTotal())
					{
						meilleureSolution = solutionCourante.copierSolution();
						améliorationTrouvée  = true;
					}
				}
				compteurItérations++;
			}

			// Mise à jour des compteurs et refroidissement
			compteurSansAmélioration = améliorationTrouvée ? 0 : compteurSansAmélioration + 1;
			température      *= coefficientRefroidissement;

			// Notifier l'IHM
			if (fonctionCallback != null)
				fonctionCallback.surItération(compteurItérations, solutionCourante, meilleureSolution, température);
		}

		long duréeMillisecondes = System.currentTimeMillis() - instantDébut;

		if (fonctionCallback != null)
				fonctionCallback.surTerminaison(meilleureSolution, duréeMillisecondes);

		return meilleureSolution;
	}

	/**
	 * Choisit un voisin selon le mode configuré.
	 * Mode MEILLEUR : explore jusqu'à 2n voisins faisables et retourne le meilleur.
	 * Mode ALÉATOIRE : retourne le premier voisin faisable trouvé.
	 * @return Un voisin faisable ou null si aucun trouvé après nombreMaxTentatives tentatives
	 */
	private VRPSolution choisirVoisin(VRPSolution solutionCourante, Random générateurAléatoire, int nombreMaxTentatives)
	{
		if (choisirMeilleurVoisin)
		{
			// Mode MEILLEUR : chercher le meilleur parmi 2n voisins faisables
			VRPSolution meilleurVoisin = null;
			int         nombreVoisinsTrouvés   = 0;

			for (int cptTentatives = 0; cptTentatives < nombreMaxTentatives * 3 && nombreVoisinsTrouvés < nombreMaxTentatives; cptTentatives++)
			{
				VRPSolution solutionVoisine = solutionCourante.générerVoisin(générateurAléatoire);

				if (solutionVoisine == null)
					continue; // Voisin infaisable, réessayer

				if (meilleurVoisin == null || solutionVoisine.getCoutTotal() < meilleurVoisin.getCoutTotal())
					meilleurVoisin = solutionVoisine;

				nombreVoisinsTrouvés++; // Compter les voisins faisables trouvés
			}

			return meilleurVoisin;
		}
		else
		{
			// Mode ALÉATOIRE : retourner le premier voisin faisable
			for (int cptTentatives = 0; cptTentatives < nombreMaxTentatives; cptTentatives++)
			{
				VRPSolution solutionVoisine = solutionCourante.générerVoisin(générateurAléatoire);

				if (solutionVoisine != null)
					return solutionVoisine;
			}

			return null; // Aucun voisin faisable après nombreMaxTentatives tentatives
		}
	}
}
