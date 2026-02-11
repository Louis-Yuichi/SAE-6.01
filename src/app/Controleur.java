package app;

import app.metier.*;
import app.ihm.*;

public class Controleur implements RecuitSimule.Callback
{
	/* ── Convertion ── */
	private Convertion convertion;

	/* ── Recuit ── */
	private VRPData        data;
	private RecuitSimule   recuit;
	private FramePrincipal framePrincipal;

	public Controleur()
	{
		this.convertion = new Convertion();
		this.framePrincipal = new FramePrincipal(this);
	}

	/* ══════ Convertion ══════ */
	public void   chargerFichiers(String src, String dst) { convertion.chargerFichiers(src, dst); }
	public String getFichierTxt(String chemin)            { return convertion.getFichierTxt(chemin); }
	public String getFichierDat(String src)               { return convertion.getFichierDat(src); }
	public int    getNbClients()                          { return convertion.getNbClients(); }
	public double getDistanceOptimal()                    { return convertion.getDistanceOptimal(); }
	public int    getQMax()                               { return convertion.getQMax(); }

	/* ══════ Recuit Simulé ══════ */
	public void chargerVRP(String fichier) {
		data = new VRPData(fichier);
		framePrincipal.getPanelRecuit().log("Chargé : " + data.getNbClients() + " clients, capacité=" +
			data.getCapacite() + ", optimal=" + data.getDistanceOptimale());
	}

	public boolean isDataCharge() { return data != null; }

	public void lancerRecuit(double t0, double tMin, double alpha,
	                         int palier, int maxSA, int maxVeh, boolean meilleur) {
		recuit = new RecuitSimule(data, t0, tMin, alpha, palier, maxSA, maxVeh, meilleur);
		recuit.setCallback(this);
		framePrincipal.getPanelRecuit().setEtatExecution(true);
		framePrincipal.getPanelRecuit().log(String.format(
			"Démarrage — T0=%.1f Tmin=%.4f α=%.2f palier=%d maxSA=%d véh=%d voisin=%s",
			t0, tMin, alpha, palier, maxSA, maxVeh, meilleur ? "meilleur" : "aléatoire"));
		new Thread(() -> recuit.executer()).start();
	}

	public void arreterRecuit() { if (recuit != null) recuit.arreter(); }

	/* ── Callback ── */
	@Override public void onIteration(int iter, VRPSolution cour, VRPSolution best, double temp) {
		framePrincipal.getPanelRecuit().majIteration(iter, cour.getCout(), best.getCout(), best.getNbTournees(), temp);
	}
	@Override public void onTermine(VRPSolution best, long ms) {
		int n = data.getNbClients();
		double[] xs = new double[n + 1], ys = new double[n + 1];
		for (int i = 0; i <= n; i++) { xs[i] = data.getX(i); ys[i] = data.getY(i); }
		framePrincipal.getPanelRecuit().majTermine(best.getCout(), best.toString(), ms, 
			data.getDistanceOptimale(), best.getNbTournees(), data.getNbClients());
		framePrincipal.getPanelRecuit().majGraphe(xs, ys, best.getTournees(), n);
	}

	/* ══════ Main ══════ */
	public static void main(String[] args) { new Controleur(); }
}