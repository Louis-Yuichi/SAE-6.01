package app.metier;

import java.io.File;
import java.util.*;

/** Données VRP : coordonnées, demandes, matrice de distances (fichier Taillard). */
public class VRPData
{
	private int nbClients;
	private int capacite;
	private double distOpt;
	private double[][] coords;
	private int[] demandes;
	private double[][] dist;

	public VRPData(String fichier)
	{
		try (Scanner sc = new Scanner(new File(fichier)))
		{
			sc.useLocale(Locale.US);
			
			nbClients = sc.nextInt();
			distOpt = sc.nextDouble();
			capacite = sc.nextInt();
			
			coords = new double[nbClients + 1][2];
			demandes = new int[nbClients + 1];
			
			sc.nextInt();
			demandes[0] = sc.nextInt(); // dépôt "0 0"
			
			for (int i = 1; i <= nbClients; i++)
			{
				sc.nextInt();
				coords[i][0] = sc.nextDouble();
				coords[i][1] = sc.nextDouble();
				demandes[i] = sc.nextInt();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur chargement : " + e.getMessage());
		}

		int n = nbClients + 1;
		dist = new double[n][n];
		
		for (int i = 0; i < n; i++)
		{
			for (int j = i + 1; j < n; j++)
			{
				double dx = coords[i][0] - coords[j][0];
				double dy = coords[i][1] - coords[j][1];
				dist[i][j] = dist[j][i] = Math.sqrt(dx * dx + dy * dy);
			}
		}
	}

	public double dist(int i, int j)
	{
		return dist[i][j];
	}

	public int getNbClients()
	{
		return nbClients;
	}

	public int getCapacite()
	{
		return capacite;
	}

	public double getDistanceOptimale()
	{
		return distOpt;
	}

	public int getDemande(int c)
	{
		return demandes[c];
	}

	public double getX(int i)
	{
		return coords[i][0];
	}

	public double getY(int i)
	{
		return coords[i][1];
	}
}
