package app.metier;

import java.io.File;
import java.util.*;

/** Données VRP : coordonnées, demandes, matrice de distances (fichier Taillard). */
public class VRPData
{
	private int nombreClients;
	private int capacité;
	private double distanceOptimale;
	private double[][] coordonnées;
	private int[] demandes;
	private double[][] matriceDistances;

	public VRPData(String cheminFichier)
	{
		try (Scanner scanneur = new Scanner(new File(cheminFichier)))
		{
			scanneur.useLocale(Locale.US);
			
			nombreClients = scanneur.nextInt();
			distanceOptimale = scanneur.nextDouble();
			capacité = scanneur.nextInt();
			
			coordonnées = new double[nombreClients + 1][2];
			demandes = new int[nombreClients + 1];
			
			scanneur.nextInt();
			demandes[0] = scanneur.nextInt(); // dépôt "0 0"
			
			for (int cpt = 1; cpt <= nombreClients; cpt++)
			{
				scanneur.nextInt();
				coordonnées[cpt][0] = scanneur.nextDouble();
				coordonnées[cpt][1] = scanneur.nextDouble();
				demandes[cpt] = scanneur.nextInt();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Erreur chargement : " + e.getMessage());
		}

		int nombreNoeuds = nombreClients + 1;
		matriceDistances = new double[nombreNoeuds][nombreNoeuds];
		
		for (int cpt1 = 0; cpt1 < nombreNoeuds; cpt1++)
		{
			for (int cpt2 = cpt1 + 1; cpt2 < nombreNoeuds; cpt2++)
			{
				double différenceX = coordonnées[cpt1][0] - coordonnées[cpt2][0];
				double différenceY = coordonnées[cpt1][1] - coordonnées[cpt2][1];
				matriceDistances[cpt1][cpt2] = matriceDistances[cpt2][cpt1] = Math.sqrt(différenceX * différenceX + différenceY * différenceY);
			}
		}
	}

	public double obtenirDistance(int noeud1, int noeud2)
	{
		return matriceDistances[noeud1][noeud2];
	}

	public int getNombreClients()
	{
		return nombreClients;
	}

	public int getCapacité()
	{
		return capacité;
	}

	public double getDistanceOptimale()
	{
		return distanceOptimale;
	}

	public int getDemande(int client)
	{
		return demandes[client];
	}

	public double getCoordX(int noeud)
	{
		return coordonnées[noeud][0];
	}

	public double getCoordY(int noeud)
	{
		return coordonnées[noeud][1];
	}
}
