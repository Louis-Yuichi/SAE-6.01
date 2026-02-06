package app.metier;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.sql.Date;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Convertion
{
	private int        nbClients;
	private double     distanceOptimal;
	private int        qMax;
	private double[][] coordonnees;
	private int[]      demandes;

	public Convertion()
	{
		
	}

	public void chargerFichiers(String fichierSource, String fichierDestination)
	{
		this.lireFichier(fichierSource);
		this.genererFichier(fichierDestination);
	}

	public String genererFichierContenu(String fichierSource)
	{
		this.lireFichier(fichierSource);
		return this.construireContenuDat();
	}

	public String lireFichierContenu(String fichierSource)
	{
		StringBuilder contenu = new StringBuilder();
		try
		{
			Scanner sc = new Scanner(new File(fichierSource));
			while (sc.hasNextLine())
			{
				contenu.append(sc.nextLine()).append("\n");
			}
			sc.close();
		}
		catch (Exception e)
		{
			contenu.append("Erreur : ").append(e.getMessage());
		}
		return contenu.toString();
	}

	public String getInfos()
	{
		return "Nombre de clients : " + this.nbClients + "\n" +
		       "Distance optimale : " + this.distanceOptimal + "\n" +
		       "Capacité max : " + this.qMax;
	}

	private void lireFichier(String fichierSource)
	{
		try
		{
			Scanner sc = new Scanner(new File(fichierSource));
			sc.useLocale(Locale.US);

			// 1. Lecture des paramètres de l'instance
			this.nbClients       = sc.nextInt();
			this.distanceOptimal = sc.nextDouble();
			this.qMax            = sc.nextInt();

			this.coordonnees = new double[this.nbClients + 1][2];
			this.demandes    = new int[this.nbClients];

			// 2. Lecture des coordonnées et de la demande
			for (int cpt = 0; cpt <= this.nbClients; cpt++)
			{
				int idClient = sc.nextInt();

				this.coordonnees[cpt][0] = sc.nextDouble(); // X
				this.coordonnees[cpt][1] = sc.nextDouble(); // Y

				int demande = sc.nextInt();
				if (cpt > 0) { this.demandes[cpt-1] = demande; } // Le dépot ne compte pas
			}
			
			sc.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur lors de la lecture du fichier source : " + e.getMessage(), e);
		}
	}

	private void genererFichier(String fichierDestination)
	{
		try
		{
			PrintWriter writer = new PrintWriter(new FileWriter(fichierDestination));
			writer.print(this.construireContenuDat());
			writer.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur lors de la génération du fichier .dat : " + e.getMessage(), e);
		}
	}

	private String construireContenuDat()
	{
		StringBuilder contenu = new StringBuilder();
		Date date = new Date(System.currentTimeMillis());
		contenu.append("/*********************************************\n");
		contenu.append(" * OPL 22.1.1.0 Model\n");
		contenu.append(" * Auteur : Groupe 5\n");
		contenu.append(" * Date   : ").append(date).append("\n");
		contenu.append(" *********************************************/\n\n");

		contenu.append("nbClient   = ").append(this.nbClients).append(";\n");
		contenu.append("nbDepot    = 1;\n");
		contenu.append("nbVehicule = 10;\n\n");
		contenu.append("qMax       = ").append(this.qMax).append(";\n\n");
		contenu.append("demande    = ").append(Arrays.toString(this.demandes)).append(";\n\n");

		contenu.append("distance   =\n[");
		for (int cpt = 0; cpt <= this.nbClients; cpt++)
		{
			contenu.append("\n\t[");
			for (int cpt2 = 0; cpt2 <= this.nbClients; cpt2++)
			{
				double distance = this.calculerDistance(cpt, cpt2);
				contenu.append(String.format(Locale.US, "%7.2f", distance));
				if (cpt2 < this.nbClients) { contenu.append(", "); }
			}

			contenu.append(cpt == this.nbClients ? "]" : "],");
		}
		contenu.append("\n];\n");

		return contenu.toString();
	}

	private double calculerDistance(int point1, int point2)
	{
		return Math.sqrt(Math.pow(this.coordonnees[point1][0] - this.coordonnees[point2][0], 2)
					   + Math.pow(this.coordonnees[point1][1] - this.coordonnees[point2][1], 2));
	}
}