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

	public String lireFichierContenu(String fichierSource)
	{
		return this.convertion.lireFichierContenu(fichierSource);
	}

	public String getInfos()
	{
		return this.convertion.getInfos();
	}

	public String genererFichierContenu(String fichierSource)
	{
		return this.convertion.genererFichierContenu(fichierSource);
	}

	public static void main(String[] args)
	{
		new Controleur();
	}
}