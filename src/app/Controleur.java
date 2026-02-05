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

	public void genererFichier(String fichierSource, String fichierDestination)
	{
		this.convertion.chargerFichiers(fichierSource, fichierDestination);
	}

	public static void main(String[] args)
	{
		new Controleur();
	}
}