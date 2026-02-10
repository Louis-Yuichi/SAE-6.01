package app.metier;

import java.util.Random;

/** Recuit simulé pour VRP. Deux modes : voisin aléatoire ou meilleur parmi 2n. */
public class RecuitSimule
{
	public interface Callback
	{
		void onIteration(int iter, VRPSolution courante, VRPSolution meilleure, double temp);
		void onTermine(VRPSolution meilleure, long tempsMs);
	}

	private          VRPData  data;
	private          double   t0, tMin, alpha;
	private          int      palier, maxSansAmelio, maxVeh;
	private          boolean  choixMeilleur;
	private          Callback callback;
	private volatile boolean arrete;

	public RecuitSimule( VRPData data, double t0, double tMin, double alpha, int palier, int maxSansAmelio, int maxVeh, boolean choixMeilleur)
	{
		this.data = data; this.t0 = t0; this.tMin = tMin; this.alpha = alpha;
		this.palier = palier; this.maxSansAmelio = maxSansAmelio;
		this.maxVeh = maxVeh; this.choixMeilleur = choixMeilleur;
	}

	public void setCallback(Callback cb) { this.callback = cb  ; }
	public void arreter()                { this.arrete   = true; }

	/**
	 * Exécute l'algorithme de recuit simulé.
	 * @return La meilleure solution trouvée
	 */
	public VRPSolution executer()
	{
		long   debut = System.currentTimeMillis();
		Random rng   = new Random();

		// 1. Solution initiale aléatoire
		VRPSolution courante  = VRPSolution.genererAleatoire(data, maxVeh, rng);
		VRPSolution meilleure = courante.copier();

		// 2. Paramètres du recuit
		double temp       = t0;                      // Température initiale
		int    sansAmelio = 0;                       // Compteur sans amélioration
		int    iter       = 0;                       // Compteur d'itérations
		int    maxTent    = 2 * data.getNbClients(); // Taille max du voisinage (2n)

		// 3. Boucle principale : tant que T > Tmin et pas trop longtemps sans amélioration
		while (temp > tMin && sansAmelio < maxSansAmelio && !arrete)
		{
			boolean ameliore = false;

			// Palier : nombre d'itérations à température constante
			for (int p = 0; p < palier && !arrete; p++)
			{
				// Chercher un voisin (aléatoire ou meilleur parmi 2n)
				VRPSolution voisine = choisirVoisin(courante, rng, maxTent);

				// Si aucun voisin faisable trouvé, passer à l'itération suivante
				if (voisine == null)
					continue;

				// Critère de Metropolis : accepter si meilleur OU avec probabilité exp(-ΔE/T)
				double dE = voisine.getCout() - courante.getCout();

				if (dE <= 0 || rng.nextDouble() < Math.exp(-dE / temp))
				{
					courante = voisine; // Accepter la solution voisine

					// Si nouveau meilleur global, sauvegarder
					if (courante.getCout() < meilleure.getCout())
					{
						meilleure = courante.copier();
						ameliore  = true;
					}
				}
				iter++;
			}

			// Mise à jour des compteurs et refroidissement
			sansAmelio = ameliore ? 0 : sansAmelio + 1;
			temp      *= alpha;

			// Notifier l'IHM
			if (callback != null)
				callback.onIteration(iter, courante, meilleure, temp);
		}

		long ms = System.currentTimeMillis() - debut;

		if (callback != null)
			callback.onTermine(meilleure, ms);

		return meilleure;
	}

	/**
	 * Choisit un voisin selon le mode configuré.
	 * Mode MEILLEUR : explore jusqu'à 2n voisins faisables et retourne le meilleur.
	 * Mode ALÉATOIRE : retourne le premier voisin faisable trouvé.
	 * @return Un voisin faisable ou null si aucun trouvé après maxTent tentatives
	 */
	private VRPSolution choisirVoisin(VRPSolution courante, Random rng, int maxTent)
	{
		if (choixMeilleur)
		{
			// Mode MEILLEUR : chercher le meilleur parmi 2n voisins faisables
			VRPSolution best = null;
			int         nb   = 0;

			for (int t = 0; t < maxTent * 3 && nb < maxTent; t++)
			{
				VRPSolution v = courante.voisin(rng);

				if (v == null)
					continue; // Voisin infaisable, réessayer

				if (best == null || v.getCout() < best.getCout())
					best = v;

				nb++; // Compter les voisins faisables trouvés
			}

			return best;
		}
		else
		{
			// Mode ALÉATOIRE : retourner le premier voisin faisable
			for (int t = 0; t < maxTent; t++)
			{
				VRPSolution v = courante.voisin(rng);

				if (v != null)
					return v;
			}

			return null; // Aucun voisin faisable après maxTent tentatives
		}
	}
}
