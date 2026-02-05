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

			Date date = new Date(System.currentTimeMillis());
			writer.println("/*********************************************");
			writer.println(" * OPL 22.1.1.0 Model");
			writer.println(" * Auteur : Groupe 5");
			writer.println(" * Date   : " + date);
			writer.println(" *********************************************/\n");

			writer.println("nbClient   = " + this.nbClients + ";");
			writer.println("nbDepot    = 1;");
			writer.println("nbVehicule = 10;\n");
			writer.println("qMax       = " + this.qMax + ";\n");
			writer.println("demande    = " + Arrays.toString(this.demandes) + ";\n");

			writer.println("distance   =");
			writer.println("[");
			for (int cpt = 0; cpt <= this.nbClients; cpt++)
			{
				writer.print("\t[");
				for (int cpt2 = 0; cpt2 <= this.nbClients; cpt2++)
				{
					double distance = this.calculerDistance(cpt, cpt2);

					writer.printf(Locale.US, "%7.2f", distance);
					if (cpt2 < this.nbClients) { writer.print(", "); }
				}

				writer.println(cpt == this.nbClients ? "]" : "],");
			}
			writer.println("];");

			writer.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur lors de la génération du fichier .dat : " + e.getMessage(), e);
		}
	}

	private double calculerDistance(int point1, int point2)
	{
		return Math.sqrt(Math.pow(this.coordonnees[point1][0] - this.coordonnees[point2][0], 2)
					   + Math.pow(this.coordonnees[point1][1] - this.coordonnees[point2][1], 2));
	}
}