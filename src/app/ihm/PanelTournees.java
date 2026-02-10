package app.ihm;

import java.awt.*;
import java.util.List;
import javax.swing.JPanel;

/** Affiche graphiquement les tournées VRP avec couleurs et dépôt. */
public class PanelTournees extends JPanel
{
	private static final Color[] COLS = {
		new Color(41, 128, 185), 
		new Color(39, 174, 96),  
		new Color(192, 57, 43),
		new Color(142, 68, 173), 
		new Color(243, 156, 18), 
		new Color(22, 160, 133),
		new Color(231, 76, 60),  
		new Color(52, 73, 94),   
		new Color(241, 196, 15),
		new Color(26, 188, 156), 
		new Color(155, 89, 182), 
		new Color(230, 126, 34),
		new Color(46, 204, 113), 
		new Color(52, 152, 219), 
		new Color(211, 84, 0)
	};
	
	private double[] xs;
	private double[] ys;
	private List<List<Integer>> tournees;
	private int nbNoeuds; // 0..nbNoeuds (depot=0)

	public void setDonnees(double[] xs, double[] ys, List<List<Integer>> tournees, int nbNoeuds) {
		this.xs = xs;
		this.ys = ys;
		this.tournees = tournees;
		this.nbNoeuds = nbNoeuds;
		repaint();
	}
	
	public void effacer() {
		this.tournees = null;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (tournees == null) {
			return;
		}
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int w = getWidth() - 60;
		int h = getHeight() - 60;

		// Calcul des bornes
		double minX = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		
		for (int i = 0; i <= nbNoeuds; i++) {
			minX = Math.min(minX, xs[i]);
			maxX = Math.max(maxX, xs[i]);
			minY = Math.min(minY, ys[i]);
			maxY = Math.max(maxY, ys[i]);
		}
		
		double rx = Math.max(maxX - minX, 1);
		double ry = Math.max(maxY - minY, 1);

		// Dessin des tournées
		for (int t = 0; t < tournees.size(); t++) {
			Color col = COLS[t % COLS.length];
			g2.setColor(col);
			g2.setStroke(new BasicStroke(2));
			
			List<Integer> tour = tournees.get(t);
			
			if (tour.isEmpty()) {
				continue;
			}
			
			int px = 30 + (int)((xs[0] - minX) / rx * w);
			int py = 30 + (int)((ys[0] - minY) / ry * h);
			int prevX = px;
			int prevY = py;
			
			for (int c : tour) {
				int cx = 30 + (int)((xs[c] - minX) / rx * w);
				int cy = 30 + (int)((ys[c] - minY) / ry * h);
				
				g2.drawLine(prevX, prevY, cx, cy);
				g2.fillOval(cx - 5, cy - 5, 10, 10);
				
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("Arial", Font.PLAIN, 9));
				g2.drawString(String.valueOf(c), cx + 6, cy - 2);
				g2.setColor(col);
				
				prevX = cx;
				prevY = cy;
			}
			
			g2.drawLine(prevX, prevY, px, py);
		}
		
		// Dessin du dépôt
		int dx = 30 + (int)((xs[0] - minX) / rx * w);
		int dy = 30 + (int)((ys[0] - minY) / ry * h);
		
		g2.setColor(Color.RED);
		g2.fillRect(dx - 7, dy - 7, 14, 14);
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 10));
		g2.drawString("D", dx - 4, dy + 4);
	}
}
