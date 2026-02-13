package app;

import app.metier.Convertion;
import app.metier.VRPData;
import app.metier.VRPSolution;
import app.metier.RecuitSimule;

import app.ihm.FramePrincipal;

public class Controleur implements RecuitSimule.Callback
{
	private Convertion convertion;

	private VRPData donneesVRP;

	private RecuitSimule recuitSimule;

	private FramePrincipal fenetrePrincipale;

	public Controleur()
	{
		this.convertion = new Convertion();
		this.fenetrePrincipale = new FramePrincipal(this);
	}

	public void chargerFichiers(String cheminSource, String cheminDestination)
	{
		this.convertion.chargerFichiers(cheminSource, cheminDestination);
	}

	public String obtenirFichierTexte(String cheminFichier)
	{
		return this.convertion.getFichierTxt(cheminFichier);
	}

	public String obtenirFichierDat(String cheminSource)
	{
		return this.convertion.getFichierDat(cheminSource);
	}

	public int obtenirNombreClients()
	{
		return this.convertion.getNbClients();
	}

	public double obtenirDistanceOptimale()
	{
		return this.convertion.getDistanceOptimal();
	}

	public int obtenirCapaciteMax()
	{
		return this.convertion.getQMax();
	}

	public void chargerVRP(String cheminFichier)
	{
		this.donneesVRP = new VRPData(cheminFichier);
		fenetrePrincipale.getPanelRecuit().ajouterLigneJournal("Charge : " + this.donneesVRP.getNombreClients()
			+ " clients, capacité=" + this.donneesVRP.getCapacite() + ", optimal=" + this.donneesVRP.getDistanceOptimale());
	}

	public boolean estDonneesChargees()
	{
		return this.donneesVRP != null;
	}

	public void lancerRecuit(double temperatureInitiale, double temperatureMinimale, double coefficientRefroidissement,
			int nombreIterationsPalier, int nombreMaxIterationsSansAmelioration, int nombreMaxVehicules,
			boolean choisirMeilleurVoisin)
	{
		this.recuitSimule = new RecuitSimule(this.donneesVRP, temperatureInitiale, temperatureMinimale,
				coefficientRefroidissement, nombreIterationsPalier, nombreMaxIterationsSansAmelioration,
				nombreMaxVehicules, choisirMeilleurVoisin);

		this.recuitSimule.definirCallback(this);

		this.fenetrePrincipale.getPanelRecuit().definirEtatExecution(true);
		this.fenetrePrincipale.getPanelRecuit().ajouterLigneJournal(
				String.format("Démarrage — T0=%.1f Tmin=%.4f α=%.2f palier=%d maxSA=%d véh=%d voisin=%s",
						temperatureInitiale, temperatureMinimale, coefficientRefroidissement, nombreIterationsPalier,
						nombreMaxIterationsSansAmelioration, nombreMaxVehicules,
						choisirMeilleurVoisin ? "meilleur" : "aléatoire"));
		new Thread(() -> this.recuitSimule.executer()).start();
	}

	public void arreterRecuit()
	{
		if (this.recuitSimule != null)
		{
			this.recuitSimule.arreter();
		}
	}

	public void surIteration(int numeroIteration, VRPSolution solutionCourante, VRPSolution meilleureSolution,
			double temperature)
	{
		fenetrePrincipale.getPanelRecuit().majIteration(numeroIteration, solutionCourante.getCoutTotal(),
				meilleureSolution.getCoutTotal(), meilleureSolution.getNombreTournees(), temperature);
	}

	public void surTerminaison(VRPSolution meilleureSolution, long dureeMillisecondes)
	{
		int nombreClients = donneesVRP.getNombreClients();

		double[] coordonneesX = new double[nombreClients + 1];
		double[] coordonneesY = new double[nombreClients + 1];

		for (int cpt = 0; cpt <= nombreClients; cpt++)
		{
			coordonneesX[cpt] = donneesVRP.getCoordX(cpt);
			coordonneesY[cpt] = donneesVRP.getCoordY(cpt);
		}

		fenetrePrincipale.getPanelRecuit().majTerminaison(meilleureSolution.getCoutTotal(),
				meilleureSolution.toString(), dureeMillisecondes, donneesVRP.getDistanceOptimale(),
				meilleureSolution.getNombreTournees(), donneesVRP.getNombreClients());

		fenetrePrincipale.getPanelRecuit().majGraphique(coordonneesX, coordonneesY, meilleureSolution.getTournees(),
				nombreClients);
	}

	public String lireFichierTexte(String cheminFichier)
	{
		return this.convertion.getFichierTxt(cheminFichier);
	}

	public String convertirTxtVersDat(String cheminSource, String cheminDestination)
	{
		this.convertion.chargerFichiers(cheminSource, cheminDestination);
		return this.convertion.getFichierDat(cheminDestination);
	}

	public static void main(String[] args)
	{
		new Controleur();
	}
}