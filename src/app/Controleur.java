package app;

import app.metier.Convertion;

import app.ihm.FramePrincipal;

public class Controleur
{
	private Convertion convertion;

	public Controleur()
	{
		this.convertion = new Convertion();
		new FramePrincipal(this);
	}

	public void chargerFichiers(String fichierSource, String fichierDestination)
	{
		this.convertion.chargerFichiers(fichierSource, fichierDestination);
	}

	public String getContenuFichier(String chemin)
	{
		return this.convertion.getContenuFichier(chemin);
	}

	public int    getNbClients()        { return this.convertion.getNbClients();       }
	public double getDistanceOptimal()  { return this.convertion.getDistanceOptimal(); }
	public int    getQMax()             { return this.convertion.getQMax();            }

	public static void main(String[] args)
	{
		new Controleur();
	}
}