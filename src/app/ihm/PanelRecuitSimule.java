package app.ihm;

import app.Controleur;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.*;

/** IHM du recuit simulé pour VRP — délègue au Controleur. */
public class PanelRecuitSimule extends JPanel implements ActionListener
{
	private Controleur ctrl;
	private JTextField txtFichier;
	private JTextField txtT0;
	private JTextField txtTMin;
	private JTextField txtAlpha;
	private JTextField txtPalier;
	private JTextField txtMaxSA;
	private JTextField txtNbVeh;
	private JCheckBox  chkMeilleur;
	private JButton    btnCharger;
	private JButton    btnLancer;
	private JButton    btnArreter;
	private JTextArea  txtLog;
	private JLabel     lblIter;
	private JLabel     lblTemp;
	private JLabel     lblCout;
	private JLabel     lblTemps;
	private JLabel     lblVehicules;
	private PanelTournees panelGraph;

	public PanelRecuitSimule(Controleur ctrl) {
		this.ctrl = ctrl;
		
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(new Color(245, 245, 250));
		
		add(panelNord(), BorderLayout.NORTH);
		add(panelCentre(), BorderLayout.CENTER);
		add(panelSud(), BorderLayout.SOUTH);
		
		btnArreter.setEnabled(false);
	}

	private JPanel panelNord() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setOpaque(false);

		// Titre
		JPanel titre = new JPanel(new BorderLayout());
		titre.setBackground(new Color(41, 128, 185));
		titre.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
		
		JLabel lbl = new JLabel("RECUIT SIMULÉ — VRP", SwingConstants.CENTER);
		lbl.setFont(new Font("Arial", Font.BOLD, 22));
		lbl.setForeground(Color.WHITE);
		titre.add(lbl);
		
		p.add(titre);
		p.add(Box.createVerticalStrut(8));

		// Initialisation des champs
		txtFichier = champ("src/app/data/tai75a.txt");
		txtT0 = champ("100");
		txtTMin = champ("0.1");
		txtAlpha = champ("0.9");
		txtPalier = champ("250000");
		txtMaxSA = champ("15");
		txtNbVeh = champ("10");
		
		btnCharger = btn("Charger", new Color(39, 174, 96));
		btnLancer  = btn("Lancer",  new Color(41, 128, 185));
		btnArreter = btn("Arrêter", new Color(192, 57, 43));

		JPanel params = new JPanel();
		params.setLayout(new BoxLayout(params, BoxLayout.Y_AXIS));
		params.setBackground(Color.WHITE);
		params.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(189, 195, 199)),
			BorderFactory.createEmptyBorder(12, 15, 12, 15)));

		// Ligne 1 : Fichier
		JPanel l1 = new JPanel(new BorderLayout(8, 0));
		l1.setOpaque(false);
		l1.add(lbl("Fichier :"), BorderLayout.WEST);
		l1.add(txtFichier, BorderLayout.CENTER);
		l1.add(btnCharger, BorderLayout.EAST);
		params.add(l1);
		params.add(Box.createVerticalStrut(10));

		// Ligne 2 : T0, Tmin, alpha
		JPanel l2 = new JPanel(new GridLayout(1, 6, 6, 0));
		l2.setOpaque(false);
		l2.add(lbl("T₀ :"));
		l2.add(txtT0);
		l2.add(lbl("T min :"));
		l2.add(txtTMin);
		l2.add(lbl("α :"));
		l2.add(txtAlpha);
		params.add(l2);
		params.add(Box.createVerticalStrut(8));

		// Ligne 3 : Palier, max sans amélio, véhicules
		JPanel l3 = new JPanel(new GridLayout(1, 6, 6, 0));
		l3.setOpaque(false);
		l3.add(lbl("Palier :"));
		l3.add(txtPalier);
		l3.add(lbl("Max sans amélio :"));
		l3.add(txtMaxSA);
		l3.add(lbl("Véhicules max :"));
		l3.add(txtNbVeh);
		params.add(l3);
		params.add(Box.createVerticalStrut(10));

		// Ligne 4 : Boutons
		JPanel l4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		l4.setOpaque(false);
		
		chkMeilleur = new JCheckBox("Meilleur voisin");
		chkMeilleur.setOpaque(false);
		chkMeilleur.setFont(new Font("Arial", Font.BOLD, 12));
		chkMeilleur.setToolTipText("Coché = meilleur parmi 2n | Décoché = aléatoire");
		
		btnLancer.setPreferredSize(new Dimension(140, 35));
		btnArreter.setPreferredSize(new Dimension(140, 35));
		
		l4.add(chkMeilleur);
		l4.add(btnLancer);
		l4.add(btnArreter);
		
		params.add(l4);
		p.add(params);
		
		return p;
	}

	private JPanel panelCentre() {
		JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
		p.setOpaque(false);
		
		txtLog = new JTextArea();
		txtLog.setEditable(false);
		txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		JScrollPane sc = new JScrollPane(txtLog);
		sc.setBorder(BorderFactory.createTitledBorder("Log"));
		
		panelGraph = new PanelTournees();
		panelGraph.setBackground(Color.WHITE);
		panelGraph.setBorder(BorderFactory.createTitledBorder("Meilleure solution"));
		
		p.add(sc);
		p.add(panelGraph);
		
		return p;
	}

	private JPanel panelSud() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		p.setBackground(new Color(52, 73, 94));
		p.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		lblIter = lblS("Itération : -");
		lblTemp = lblS("Température : -");
		lblCout = lblS("Meilleur coût : -");
		lblVehicules = lblS("Véhicules : -");
		lblTemps = lblS("Temps : -");
		
		p.add(lblIter);
		p.add(lblTemp);
		p.add(lblCout);
		p.add(lblVehicules);
		p.add(lblTemps);
		
		return p;
	}

	// --- Utilitaires ---
	private JTextField champ(String v) {
		JTextField t = new JTextField(v);
		t.setFont(new Font("Monospaced", Font.PLAIN, 13));
		t.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(189, 195, 199)),
			BorderFactory.createEmptyBorder(4, 6, 4, 6)));
		return t;
	}
	
	private JLabel lbl(String t) {
		JLabel l = new JLabel(t, SwingConstants.RIGHT);
		l.setFont(new Font("Arial", Font.BOLD, 12));
		return l;
	}
	
	private JLabel lblS(String t) {
		JLabel l = new JLabel(t);
		l.setForeground(Color.WHITE);
		l.setFont(new Font("Arial", Font.BOLD, 13));
		return l;
	}
	
	private JButton btn(String t, Color bg) {
		JButton b = new JButton(t);
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.setFocusPainted(false);
		b.setFont(new Font("Arial", Font.BOLD, 13));
		b.addActionListener(this);
		return b;
	}

	// --- Actions ---
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCharger) {
			try {
				ctrl.chargerVRP(txtFichier.getText().trim());
			} catch (Exception ex) {
				log("ERREUR : " + ex.getMessage());
			}
		}
		
		if (e.getSource() == btnLancer) {
			lancer();
		}
		
		if (e.getSource() == btnArreter) {
			ctrl.arreterRecuit();
		}
	}

	private void lancer() {
		if (!ctrl.isDataCharge()) {
			try {
				ctrl.chargerVRP(txtFichier.getText().trim());
			} catch (Exception ex) {
				log("ERREUR : " + ex.getMessage());
				return;
			}
		}
		
		double t0 = Double.parseDouble(txtT0.getText().trim());
		double tm = Double.parseDouble(txtTMin.getText().trim());
		double al = Double.parseDouble(txtAlpha.getText().trim());
		int pal = Integer.parseInt(txtPalier.getText().trim());
		int msa = Integer.parseInt(txtMaxSA.getText().trim());
		int veh = Integer.parseInt(txtNbVeh.getText().trim());
		
		txtLog.setText("");
		panelGraph.effacer();
		
		ctrl.lancerRecuit(t0, tm, al, pal, msa, veh, chkMeilleur.isSelected());
	}

	/* ── Méthodes appelées par le Controleur ── */
	public void log(String msg) {
		txtLog.append(msg + "\n");
		txtLog.setCaretPosition(txtLog.getDocument().getLength());
	}

	public void setEtatExecution(boolean enCours) {
		btnLancer.setEnabled(!enCours);
		btnArreter.setEnabled(enCours);
	}

	public void majIteration(int iter, double coutCour, double coutBest, int nbT, double temp) {
		SwingUtilities.invokeLater(() -> {
			lblIter.setText("Itération : " + iter);
			lblTemp.setText(String.format("Température : %.4f", temp));
			lblCout.setText(String.format("Meilleur coût : %.2f", coutBest));
			lblVehicules.setText("Véhicules : " + nbT);
			log(String.format("iter=%-8d T=%-12.4f courante=%-10.2f meilleure=%-10.2f tournées=%d",
				iter, temp, coutCour, coutBest, nbT));
		});
	}

	public void majTermine(double cout, String detail, long ms, double optimal, int nbVehicules, int nbClients) {
		SwingUtilities.invokeLater(() -> {
			lblTemps.setText(String.format("Temps : %d ms (%.2f s)", ms, ms / 1000.0));
			setEtatExecution(false);
			log("═══════════════════════════════════════════════════════=");
			log(String.format("TERMINÉ en %d ms — Coût : %.2f (optimal : %.2f)", ms, cout, optimal));
			log(String.format("Véhicules utilisés : %d", nbVehicules));
			log(String.format("Clients servis : %d", nbClients));
			log(String.format("Coût total de la solution : %.2f", cout));
			log("─────────────────────────────────────────────────────────");
			log(detail);
		});
	}

	public void majGraphe(double[] xs, double[] ys, List<List<Integer>> tournees, int nbNoeuds) {
		SwingUtilities.invokeLater(() -> panelGraph.setDonnees(xs, ys, tournees, nbNoeuds));
	}
}
