package app.metier;

import java.io.File;

import java.util.Scanner;
import java.util.Locale;

public class VRPData
{
	private int nombreClients;
	private int capacite;

	private double     distanceOptimale;
	private double[][] coordonnees;

	private int[]      demandes;

	private double[][] matriceDistances;

	public VRPData(String cheminFichier)
	{
		try (Scanner sc = new Scanner(new File(cheminFichier)))
		{
			sc.useLocale(Locale.US);
			
			this.nombreClients    = sc.nextInt();
			this.distanceOptimale = sc.nextDouble();
			this.capacite         = sc.nextInt();
			
			this.coordonnees = new double[this.nombreClients + 1][2];
			this.demandes    = new int   [this.nombreClients + 1];

			sc.nextInt();
			this.demandes[0] = sc.nextInt();
			
			for (int cpt = 1; cpt <= this.nombreClients; cpt++)
			{
				sc.nextInt();
				this.coordonnees[cpt][0] = sc.nextDouble();
				this.coordonnees[cpt][1] = sc.nextDouble();
				this.demandes   [cpt]    = sc.nextInt();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur chargement : " + e.getMessage());
		}

		int nombreNoeuds = this.nombreClients + 1;

		this.matriceDistances = new double[nombreNoeuds][nombreNoeuds];

		for (int cpt1 = 0; cpt1 < nombreNoeuds; cpt1++)
		{
			for (int cpt2 = cpt1 + 1; cpt2 < nombreNoeuds; cpt2++)
			{
				double differenceX = this.coordonnees[cpt1][0] - this.coordonnees[cpt2][0];
				double differenceY = this.coordonnees[cpt1][1] - this.coordonnees[cpt2][1];

				this.matriceDistances[cpt1][cpt2] = this.matriceDistances[cpt2][cpt1] = Math.sqrt(differenceX * differenceX + differenceY * differenceY);
			}
		}
	}

	public double obtenirDistance(int noeud1, int noeud2)
	{
		return this.matriceDistances[noeud1][noeud2];
	}

	public int    getNombreClients()     { return this.nombreClients;         }
	public int    getCapacite()          { return this.capacite;              }

	public double getDistanceOptimale()  { return this.distanceOptimale;      }

	public int    getDemande(int client) { return this.demandes[client];      }

	public double getCoordX(int noeud)   { return this.coordonnees[noeud][0]; }
	public double getCoordY(int noeud)   { return this.coordonnees[noeud][1]; }
}