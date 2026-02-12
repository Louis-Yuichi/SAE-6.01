package app.ihm;

import java.util.List;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

import javax.swing.JPanel;

public class PanelGraphiqueTournees extends JPanel
{
	private static final Color[] PALETTE_COULEURS =
	{
		new Color(41, 128, 185),
		new Color(39, 174, 96 ),
		new Color(192, 57, 43 ),
		new Color(142, 68, 173),
		new Color(243, 156, 18),
		new Color(22, 160, 133),
		new Color(231, 76, 60 ),
		new Color(52, 73, 94  ),
		new Color(241, 196, 15),
		new Color(26, 188, 156),
		new Color(155, 89, 182),
		new Color(230, 126, 34),
		new Color(46, 204, 113),
		new Color(52, 152, 219),
		new Color(211, 84, 0  )
	};
	
	private double[] coordonneesX;
	private double[] coordonneesY;

	private List<List<Integer>> listeTournees;

	private int nombreNoeuds;

	public void definirDonnees(double[] coordonneesX, double[] coordonneesY, List<List<Integer>> listeTournees, int nombreNoeuds)
	{
		this.coordonneesX  = coordonneesX;
		this.coordonneesY  = coordonneesY;
		this.listeTournees = listeTournees;
		this.nombreNoeuds  = nombreNoeuds;

		this.repaint();
	}
	
	public void effacer()
	{
		this.coordonneesX  = null;
		this.coordonneesY  = null;
		this.listeTournees = null;
		this.nombreNoeuds  = 0;

		this.repaint();
	}

	protected void paintComponent(Graphics graphique)
	{
		super.paintComponent(graphique);

		if (this.coordonneesX == null || this.coordonneesY == null || this.listeTournees == null || this.nombreNoeuds == 0)
		{
			return;
		}

		Graphics2D graphique2D = (Graphics2D) graphique;
		graphique2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double coordXMinimale = this.coordonneesX[0];
		double coordXMaximale = this.coordonneesX[0];
		double coordYMinimale = this.coordonneesY[0];
		double coordYMaximale = this.coordonneesY[0];

		for (int cpt = 1; cpt < this.nombreNoeuds; cpt++)
		{
			if (this.coordonneesX[cpt] < coordXMinimale) coordXMinimale = this.coordonneesX[cpt];
			if (this.coordonneesX[cpt] > coordXMaximale) coordXMaximale = this.coordonneesX[cpt];
			if (this.coordonneesY[cpt] < coordYMinimale) coordYMinimale = this.coordonneesY[cpt];
			if (this.coordonneesY[cpt] > coordYMaximale) coordYMaximale = this.coordonneesY[cpt];
		}

		int largeur = this.getWidth()  - 60;
		int hauteur = this.getHeight() - 60;

		double ratioX = (coordXMaximale - coordXMinimale) / largeur;
		double ratioY = (coordYMaximale - coordYMinimale) / hauteur;
		
		int couleurIndex = 0;
		for (List<Integer> tournee : this.listeTournees)
		{
			graphique2D.setColor(PALETTE_COULEURS[couleurIndex % PALETTE_COULEURS.length]);
			graphique2D.setStroke(new BasicStroke(2.0f));
			
			int noeudPrecedent = 0;
			for (int noeud : tournee)
			{
				int coordXDébut = 30 + (int)((this.coordonneesX[noeudPrecedent] - coordXMinimale) / ratioX * largeur);
				int coordYDébut = 30 + (int)((this.coordonneesY[noeudPrecedent] - coordYMinimale) / ratioY * hauteur);
				int coordXFin   = 30 + (int)((this.coordonneesX[noeud]          - coordXMinimale) / ratioX * largeur);
				int coordYFin   = 30 + (int)((this.coordonneesY[noeud]          - coordYMinimale) / ratioY * hauteur);

				graphique2D.drawLine(coordXDébut, coordYDébut, coordXFin, coordYFin);
				noeudPrecedent = noeud;
			}
			
			int coordXDébut = 30 + (int)((this.coordonneesX[noeudPrecedent] - coordXMinimale) / ratioX * largeur);
			int coordYDébut = 30 + (int)((this.coordonneesY[noeudPrecedent] - coordYMinimale) / ratioY * hauteur);
			int coordXFin   = 30 + (int)((this.coordonneesX[0]              - coordXMinimale) / ratioX * largeur);
			int coordYFin   = 30 + (int)((this.coordonneesY[0]              - coordYMinimale) / ratioY * hauteur);

			graphique2D.drawLine(coordXDébut, coordYDébut, coordXFin, coordYFin);

			couleurIndex++;
		}
		
		for (int cpt = 1; cpt < this.nombreNoeuds; cpt++)
		{
			int coordX = 30 + (int)((this.coordonneesX[cpt] - coordXMinimale) / ratioX * largeur);
			int coordY = 30 + (int)((this.coordonneesY[cpt] - coordYMinimale) / ratioY * hauteur);

			graphique2D.setColor(Color.BLACK);
			graphique2D.fillOval(coordX - 4, coordY - 4, 8, 8);
		}
		
		int coordXDepot = 30 + (int)((this.coordonneesX[0] - coordXMinimale) / ratioX * largeur);
		int coordYDepot = 30 + (int)((this.coordonneesY[0] - coordYMinimale) / ratioY * hauteur);
		
		graphique2D.setColor(Color.RED);
		graphique2D.fillRect(coordXDepot - 7, coordYDepot - 7, 14, 14);
		
		graphique2D.setColor(Color.WHITE);
		graphique2D.setFont(new Font("Arial", Font.BOLD, 10));
		graphique2D.drawString("D", coordXDepot - 4, coordYDepot + 4);
	}
}