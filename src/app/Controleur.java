package app;

import app.metier.Convertion;
import app.metier.VRPData;
import app.metier.VRPSolution;
import app.metier.RecuitSimule;

import app.ihm.FramePrincipal;

public class Controleur implements RecuitSimule.Callback
{
	private Convertion     convertion;
	private VRPData        donnéesVRP;
	private RecuitSimule   recuitSimulé;

	private FramePrincipal framePrincipal;

	public Controleur()
	{
		this.convertion     = new Convertion();
		this.framePrincipal = new FramePrincipal(this);
	}

	public void chargerFichiers(String source, String destination)
	{
		convertion.chargerFichiers(source, destination);
	}

	public String obtenirFichierTexte(String chemin)
	{
		return convertion.getFichierTxt(chemin);
	}

	public String obtenirFichierDat(String source)
	{
		return convertion.getFichierDat(source);
	}

	public int obtenirNombreClients()
	{
		return convertion.getNbClients();
	}

	public double obtenirDistanceOptimale()
	{
		return convertion.getDistanceOptimal();
	}

	public int obtenirCapacitéMax()
	{
		return convertion.getQMax();
	}

	public void chargerVRP(String cheminFichier)
	{
		donnéesVRP = new VRPData(cheminFichier);
		framePrincipal.getPanelRecuit().log("Chargé : " + donnéesVRP.getNombreClients() + " clients, capacité="
														+ donnéesVRP.getCapacité() + ", optimal=" + donnéesVRP.getDistanceOptimale());
	}

	public boolean estDonnéesChargées()
	{
		return donnéesVRP != null;
	}

	public void lancerRecuit(double températureInitiale, double températureMinimale, double coefficientRefroidissement, int nombreItérationsPalier, int nombreMaxItérationsSansAmélioration, int nombreMaxVéhicules, boolean choisirMeilleurVoisin)
	{
		recuitSimulé = new RecuitSimule(donnéesVRP, températureInitiale, températureMinimale, coefficientRefroidissement, nombreItérationsPalier, nombreMaxItérationsSansAmélioration, nombreMaxVéhicules, choisirMeilleurVoisin);
		recuitSimulé.définirCallback(this);
		framePrincipal.getPanelRecuit().setEtatExecution(true);
		framePrincipal.getPanelRecuit().log(String.format("Démarrage — T0=%.1f Tmin=%.4f α=%.2f palier=%d maxSA=%d véh=%d voisin=%s",
														   températureInitiale, températureMinimale, coefficientRefroidissement, nombreItérationsPalier, nombreMaxItérationsSansAmélioration, nombreMaxVéhicules, choisirMeilleurVoisin ? "meilleur" : "aléatoire"));
		new Thread(() -> recuitSimulé.exécuter()).start();
	}

	public void arrêterRecuit()
	{
		if (recuitSimulé != null) recuitSimulé.arrêter();
	}

	public void surItération(int numéroItération, VRPSolution solutionCourante, VRPSolution meilleureSolution, double température)
	{
		framePrincipal.getPanelRecuit().majIteration(numéroItération, solutionCourante.getCoutTotal(), meilleureSolution.getCoutTotal(), meilleureSolution.getNombreTournées(), température);
	}

	public void surTerminaison(VRPSolution meilleureSolution, long duréeMillisecondes)
	{
		int nombreClients = donnéesVRP.getNombreClients();

		double[] coordonnéesX = new double[nombreClients + 1], coordonnéesY = new double[nombreClients + 1];

		for (int cpt = 0; cpt <= nombreClients; cpt++)
		{
			coordonnéesX[cpt] = donnéesVRP.getCoordX(cpt);
			coordonnéesY[cpt] = donnéesVRP.getCoordY(cpt);
		}

		framePrincipal.getPanelRecuit().majTermine(meilleureSolution.getCoutTotal(), meilleureSolution.toString(), duréeMillisecondes, donnéesVRP.getDistanceOptimale(), meilleureSolution.getNombreTournées(), donnéesVRP.getNombreClients());
		framePrincipal.getPanelRecuit().majGraphe(coordonnéesX, coordonnéesY, meilleureSolution.getTournées(), nombreClients);
	}

	public static void main(String[] args)
	{
		new Controleur();
	}
}