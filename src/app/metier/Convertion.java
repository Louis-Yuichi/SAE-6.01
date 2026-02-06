package app.metier;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.nio.file.Files;
import java.nio.file.Path;

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
		this.nbClients       = 0;
		this.distanceOptimal = 0.0;
		this.qMax            = 0;
		this.coordonnees     = null;
		this.demandes        = null;
	}

	public void chargerFichiers(String fichierSource, String fichierDestination)
	{
		this.lireFichier(fichierSource);
		this.genererFichier(fichierDestination);
	}

	public String getContenuFichier(String chemin)
	{
		try
		{
			return Files.readString(Path.of(chemin));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur lors de la lecture du fichier : " + e.getMessage(), e);
		}
	}

	public int    getNbClients()       { return this.nbClients;       }
	public double getDistanceOptimal() { return this.distanceOptimal; }
	public int    getQMax()            { return this.qMax;            }

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
				sc.nextInt();

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
		try (PrintWriter writer = new PrintWriter(new FileWriter(fichierDestination)))
		{
			writer.println("/*********************************************");
			writer.println(" * OPL 22.1.1.0 Model");
			writer.println(" * Auteur : Groupe 5");
			writer.println(" * Date   : " + java.time.LocalDate.now());
			writer.println(" *********************************************/");
			writer.println();
			
			writer.println("nbClient   = " + this.nbClients + ";");
			writer.println("nbDepot    = 1;");
			writer.println("nbVehicule = 10;");
			writer.println();
			writer.println("qMax       = " + this.qMax + ";");
			writer.println();
			writer.println("demandes   = " + Arrays.toString(this.demandes) + ";");
			writer.println();
			
			writer.println("distances  =");
			writer.print("[");
			for (int cpt = 0; cpt <= this.nbClients; cpt++)
			{
				writer.print("\n\t[");
				for (int cpt2 = 0; cpt2 <= this.nbClients; cpt2++)
				{
					double distance = this.calculerDistance(cpt, cpt2);

					writer.printf(Locale.US, "%7.2f", distance);

					if (cpt2 < this.nbClients) writer.print(", ");
				}

				writer.print(cpt == this.nbClients ? "]" : "],");
			}

			writer.println("\n];");
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