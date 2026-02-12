package app.metier;

import java.util.Random;

public class RecuitSimule
{
	public interface Callback
	{
		void surIteration(int numeroIteration, VRPSolution solutionCourante, VRPSolution meilleureSolution, double temperature);
		void surTerminaison(VRPSolution meilleureSolution, long dureeMillisecondes);
	}

	private VRPData  donneesVRP;

	private double   temperatureInitiale;
	private double   temperatureMinimale;
	private double   coefficientRefroidissement;

	private int      nombreIterationsPalier;
	private int      nombreMaxIterationsSansAmelioration;
	private int      nombreMaxVehicules;

	private boolean  choisirMeilleurVoisin;

	private Callback fonctionCallback;

	private volatile boolean estArrete;

	public RecuitSimule(VRPData donneesVRP, double temperatureInitiale, double temperatureMinimale, double coefficientRefroidissement,
						int nombreIterationsPalier, int nombreMaxIterationsSansAmelioration, int nombreMaxVehicules, boolean choisirMeilleurVoisin)
	{
		this.donneesVRP                          = donneesVRP;
		this.temperatureInitiale                 = temperatureInitiale;
		this.temperatureMinimale                 = temperatureMinimale;
		this.coefficientRefroidissement          = coefficientRefroidissement;
		this.nombreIterationsPalier              = nombreIterationsPalier;
		this.nombreMaxIterationsSansAmelioration = nombreMaxIterationsSansAmelioration;
		this.nombreMaxVehicules                  = nombreMaxVehicules;
		this.choisirMeilleurVoisin               = choisirMeilleurVoisin;
	}

	public void definirCallback(Callback callback)
	{
		this.fonctionCallback = callback;
	}

	public void arreter()
	{
		this.estArrete = true;
	}

	public VRPSolution executer()
	{
		long instantDebut = System.currentTimeMillis();

		Random generateurAleatoire    = new Random();

		VRPSolution solutionCourante  = VRPSolution.genererSolutionInitiale(this.donneesVRP, this.nombreMaxVehicules, generateurAleatoire);
		VRPSolution meilleureSolution = solutionCourante.copierSolution();

		double temperature           = this.temperatureInitiale;
		int compteurSansAmelioration = 0;
		int compteurIterations       = 0;
		int nombreMaxTentatives      = 2 * this.donneesVRP.getNombreClients();

		while (temperature > this.temperatureMinimale && compteurSansAmelioration < this.nombreMaxIterationsSansAmelioration && !this.estArrete)
		{
			boolean ameliorationTrouvee = false;

			for (int cptPalier = 0; cptPalier < this.nombreIterationsPalier && !this.estArrete; cptPalier++)
			{
				VRPSolution solutionVoisine = this.choisirVoisin(solutionCourante, generateurAleatoire, nombreMaxTentatives);

				if (solutionVoisine == null)
				{
					continue;
				}

				double differenceCout = solutionVoisine.getCoutTotal() - solutionCourante.getCoutTotal();

				if (differenceCout <= 0 || generateurAleatoire.nextDouble() < Math.exp(-differenceCout / temperature))
				{
					solutionCourante = solutionVoisine;

					if (solutionCourante.getCoutTotal() < meilleureSolution.getCoutTotal())
					{
						meilleureSolution = solutionCourante.copierSolution();
						ameliorationTrouvee = true;
					}
				}

				compteurIterations++;
			}

			compteurSansAmelioration = ameliorationTrouvee ? 0 : compteurSansAmelioration + 1;
			temperature *= this.coefficientRefroidissement;

			if (this.fonctionCallback != null)
			{
				this.fonctionCallback.surIteration(compteurIterations, solutionCourante, meilleureSolution, temperature);
			}
		}

		long dureeMillisecondes = System.currentTimeMillis() - instantDebut;

		if (this.fonctionCallback != null)
		{
			this.fonctionCallback.surTerminaison(meilleureSolution, dureeMillisecondes);
		}

		return meilleureSolution;
	}

	private VRPSolution choisirVoisin(VRPSolution solutionCourante, Random generateurAleatoire, int nombreMaxTentatives)
	{
		if (this.choisirMeilleurVoisin)
		{
			VRPSolution meilleurVoisin = null;
			int nombreVoisinsTrouves   = 0;

			for (int cptTentatives = 0; cptTentatives < nombreMaxTentatives * 3 && nombreVoisinsTrouves < nombreMaxTentatives; cptTentatives++)
			{
				VRPSolution solutionVoisine = solutionCourante.genererVoisin(generateurAleatoire);

				if (solutionVoisine == null)
				{
					continue;
				}

				if (meilleurVoisin == null || solutionVoisine.getCoutTotal() < meilleurVoisin.getCoutTotal())
				{
					meilleurVoisin = solutionVoisine;
				}

				nombreVoisinsTrouves++;
			}

			return meilleurVoisin;
		}
		else
		{
			for (int cptTentatives = 0; cptTentatives < nombreMaxTentatives; cptTentatives++)
			{
				VRPSolution solutionVoisine = solutionCourante.genererVoisin(generateurAleatoire);

				if (solutionVoisine != null)
				{
					return solutionVoisine;
				}
			}

			return null;
		}
	}
}