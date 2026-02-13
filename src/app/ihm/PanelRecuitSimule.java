package app.ihm;

import app.Controleur;

import java.util.List;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PanelRecuitSimule extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private PanelGraphiqueTournees panelGraphique;

	private JTextField txtFichier;
	private JTextField txtTemperatureInitiale;
	private JTextField txtTemperatureMinimale;
	private JTextField txtCoefficient;
	private JTextField txtPalier;
	private JTextField txtMaxSansAmelioration;
	private JTextField txtNombreVehicules;

	private JCheckBox  chkMeilleurVoisin;

	private JButton    btnCharger;
	private JButton    btnParcourir;
	private JButton    btnLancer;
	private JButton    btnArreter;

	private JTextArea  txtJournal;

	private JLabel     lblIteration;
	private JLabel     lblTemperature;
	private JLabel     lblCout;
	private JLabel     lblVehicules;
	private JLabel     lblTempsDuree;

	private JFileChooser fileChooser;

	public PanelRecuitSimule(Controleur ctrl)
	{
		this.ctrl = ctrl;
		
		this.setLayout(new BorderLayout(10, 10));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setBackground(new Color(245, 245, 250));

		this.fileChooser = new JFileChooser();
		this.fileChooser.setCurrentDirectory(new File("src/app/data"));
		this.fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt"));
		
		this.add(this.creerPanelSuperieur(), BorderLayout.NORTH);
		this.add(this.creerPanelCentral(), BorderLayout.CENTER);
		this.add(this.creerPanelInferieur(), BorderLayout.SOUTH);
		
		this.btnArreter.setEnabled(false);
	}

	private JPanel creerPanelSuperieur()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(false);

		JPanel panelTitre = new JPanel(new BorderLayout());
		panelTitre.setBackground(new Color(41, 128, 185));
		panelTitre.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
		
		JLabel lblTitre = new JLabel("RECUIT SIMULe — VRP", SwingConstants.CENTER);
		lblTitre.setFont(new Font("Arial", Font.BOLD, 22));
		lblTitre.setForeground(Color.WHITE);
		panelTitre.add(lblTitre);
		
		panel.add(panelTitre);
		panel.add(Box.createVerticalStrut(8));

		this.txtFichier             = this.creerChampTexte("src/app/data/tai75a.txt");
		this.txtTemperatureInitiale = this.creerChampTexte("100");
		this.txtTemperatureMinimale = this.creerChampTexte("0.1");
		this.txtCoefficient         = this.creerChampTexte("0.9");
		this.txtPalier              = this.creerChampTexte("250000");
		this.txtMaxSansAmelioration = this.creerChampTexte("15");
		this.txtNombreVehicules     = this.creerChampTexte("10");
		
		this.btnCharger   = this.creerBouton("Charger",   new Color(39, 174, 96));
		this.btnParcourir = this.creerBouton("Parcourir", new Color(142, 68, 173));
		this.btnLancer    = this.creerBouton("Lancer",    new Color(41, 128, 185));
		this.btnArreter   = this.creerBouton("Arrêter",   new Color(192, 57, 43));

		JPanel panelParamètres = new JPanel();
		panelParamètres.setLayout(new BoxLayout(panelParamètres, BoxLayout.Y_AXIS));
		panelParamètres.setBackground(Color.WHITE);
		panelParamètres.setBorder(BorderFactory.createCompoundBorder
		(
			BorderFactory.createLineBorder(new Color(189, 195, 199)),
			BorderFactory.createEmptyBorder(12, 15, 12, 15)
		));

		JPanel panelLigneFichier = new JPanel(new BorderLayout(8, 0));
		panelLigneFichier.setOpaque(false);
		panelLigneFichier.add(this.creerEtiquette("Fichier :"), BorderLayout.WEST);
		panelLigneFichier.add(this.txtFichier, BorderLayout.CENTER);
		
		JPanel panelBoutonsFichier = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panelBoutonsFichier.setOpaque(false);
		panelBoutonsFichier.add(this.btnParcourir);
		panelBoutonsFichier.add(this.btnCharger);
		panelLigneFichier.add(panelBoutonsFichier, BorderLayout.EAST);
		
		panelParamètres.add(panelLigneFichier);
		panelParamètres.add(Box.createVerticalStrut(10));

		JPanel panelLigneTemperatures = new JPanel(new GridLayout(1, 6, 6, 0));
		panelLigneTemperatures.setOpaque(false);
		panelLigneTemperatures.add(this.creerEtiquette("T₀ :"));
		panelLigneTemperatures.add(this.txtTemperatureInitiale);
		panelLigneTemperatures.add(this.creerEtiquette("T min :"));
		panelLigneTemperatures.add(this.txtTemperatureMinimale);
		panelLigneTemperatures.add(this.creerEtiquette("α :"));
		panelLigneTemperatures.add(this.txtCoefficient);
		panelParamètres.add(panelLigneTemperatures);
		panelParamètres.add(Box.createVerticalStrut(8));

		JPanel panelLigneIterations = new JPanel(new GridLayout(1, 6, 6, 0));
		panelLigneIterations.setOpaque(false);
		panelLigneIterations.add(this.creerEtiquette("Palier :"));
		panelLigneIterations.add(this.txtPalier);
		panelLigneIterations.add(this.creerEtiquette("Max sans amelio :"));
		panelLigneIterations.add(this.txtMaxSansAmelioration);
		panelLigneIterations.add(this.creerEtiquette("Vehicules max :"));
		panelLigneIterations.add(this.txtNombreVehicules);
		panelParamètres.add(panelLigneIterations);
		panelParamètres.add(Box.createVerticalStrut(10));

		JPanel panelLigneBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		panelLigneBoutons.setOpaque(false);
		
		this.chkMeilleurVoisin = new JCheckBox("Meilleur voisin");
		this.chkMeilleurVoisin.setOpaque(false);
		this.chkMeilleurVoisin.setFont(new Font("Arial", Font.BOLD, 12));
		this.chkMeilleurVoisin.setToolTipText("Coche = meilleur parmi 2n | Decoche = aleatoire");
		
		this.btnLancer.setPreferredSize(new Dimension(140, 35));
		this.btnArreter.setPreferredSize(new Dimension(140, 35));
		
		panelLigneBoutons.add(this.chkMeilleurVoisin);
		panelLigneBoutons.add(this.btnLancer);
		panelLigneBoutons.add(this.btnArreter);
		
		panelParamètres.add(panelLigneBoutons);
		panel.add(panelParamètres);

		return panel;
	}

	private JPanel creerPanelCentral()
	{
		JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
		panel.setOpaque(false);
		
		this.txtJournal = new JTextArea();
		this.txtJournal.setEditable(false);
		this.txtJournal.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		JScrollPane scrollJournal = new JScrollPane(this.txtJournal);
		scrollJournal.setBorder(BorderFactory.createTitledBorder("Journal"));
		
		this.panelGraphique = new PanelGraphiqueTournees();
		this.panelGraphique.setBackground(Color.WHITE);
		this.panelGraphique.setBorder(BorderFactory.createTitledBorder("Meilleure solution"));
		this.panelGraphique.setPreferredSize(new Dimension(400, 400));
		
		panel.add(scrollJournal);
		panel.add(this.panelGraphique);
		
		return panel;
	}

	private JPanel creerPanelInferieur()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
		panel.setBackground(new Color(52, 73, 94));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		this.lblIteration   = this.creerEtiquetteStatut("Iteration : -");
		this.lblTemperature = this.creerEtiquetteStatut("Temperature : -");
		this.lblCout        = this.creerEtiquetteStatut("Meilleur coût : -");
		this.lblVehicules   = this.creerEtiquetteStatut("Vehicules : -");
		this.lblTempsDuree  = this.creerEtiquetteStatut("Temps : -");
		
		panel.add(this.lblIteration);
		panel.add(this.lblTemperature);
		panel.add(this.lblCout);
		panel.add(this.lblVehicules);
		panel.add(this.lblTempsDuree);
		
		return panel;
	}

	private JTextField creerChampTexte(String valeurInitiale)
	{
		JTextField txt = new JTextField(valeurInitiale);
		txt.setFont(new Font("Monospaced", Font.PLAIN, 13));
		txt.setBorder(BorderFactory.createCompoundBorder
		(
			BorderFactory.createLineBorder(new Color(189, 195, 199)),
			BorderFactory.createEmptyBorder(4, 6, 4, 6)
		));

		return txt;
	}
	
	private JLabel creerEtiquette(String texte)
	{
		JLabel lbl = new JLabel(texte, SwingConstants.RIGHT);
		lbl.setFont(new Font("Arial", Font.BOLD, 12));

		return lbl;
	}
	
	private JLabel creerEtiquetteStatut(String texte)
	{
		JLabel lbl = new JLabel(texte);
		lbl.setForeground(Color.WHITE);
		lbl.setFont(new Font("Arial", Font.BOLD, 13));

		return lbl;
	}
	
	private JButton creerBouton(String texte, Color couleurFond)
	{
		JButton btn = new JButton(texte);
		btn.setBackground(couleurFond);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setFont(new Font("Arial", Font.BOLD, 13));
		btn.addActionListener(this);

		return btn;
	}

	public void actionPerformed(ActionEvent evenement)
	{
		if (evenement.getSource() == this.btnParcourir)
		{
			int resultat = this.fileChooser.showOpenDialog(this);
			if (resultat == JFileChooser.APPROVE_OPTION)
			{
				File fichierSelectionne = this.fileChooser.getSelectedFile();
				this.txtFichier.setText(fichierSelectionne.getPath());
				this.ajouterLigneJournal("Fichier sélectionné : " + fichierSelectionne.getName());
			}
		}

		if (evenement.getSource() == this.btnCharger)
		{
			try
			{
				this.ctrl.chargerVRP(this.txtFichier.getText().trim());
			}
			catch (Exception exception)
			{
				this.ajouterLigneJournal("ERREUR : " + exception.getMessage());
			}
		}
		
		if (evenement.getSource() == this.btnLancer)
		{
			this.lancerRecuitSimule();
		}
		
		if (evenement.getSource() == this.btnArreter)
		{
			this.ctrl.arreterRecuit();
		}
	}

	private void lancerRecuitSimule()
	{
		if (!this.ctrl.estDonneesChargees())
		{
			try
			{
				this.ctrl.chargerVRP(this.txtFichier.getText().trim());
			}
			catch (Exception exception)
			{
				this.ajouterLigneJournal("ERREUR : " + exception.getMessage());
				return;
			}
		}
		
		double temperatureInitiale        = Double.parseDouble(this.txtTemperatureInitiale.getText().trim());
		double temperatureMinimale        = Double.parseDouble(this.txtTemperatureMinimale.getText().trim());
		double coefficientRefroidissement = Double.parseDouble(this.txtCoefficient.getText().trim());

		int nombreIterationsPalier              = Integer.parseInt(this.txtPalier.getText().trim());
		int nombreMaxIterationsSansAmelioration = Integer.parseInt(this.txtMaxSansAmelioration.getText().trim());
		int nombreMaxVehicules                  = Integer.parseInt(this.txtNombreVehicules.getText().trim());
		
		this.txtJournal.setText("");
		this.panelGraphique.effacer();
		
		this.ctrl.lancerRecuit(temperatureInitiale, temperatureMinimale, coefficientRefroidissement,
							nombreIterationsPalier, nombreMaxIterationsSansAmelioration, nombreMaxVehicules,
							this.chkMeilleurVoisin.isSelected());
	}

	public void ajouterLigneJournal(String message)
	{
		this.txtJournal.append(message + "\n");
		this.txtJournal.setCaretPosition(this.txtJournal.getDocument().getLength());
	}

	public void definirEtatExecution(boolean estEnCours)
	{
		this.btnLancer.setEnabled(!estEnCours);
		this.btnArreter.setEnabled(estEnCours);
	}

	public void majIteration(int numeroIteration, double coutCourant, double meilleurCout, int nombreTournees, double temperature)
	{
		SwingUtilities.invokeLater(() ->
		{
			this.lblIteration.setText("Iteration : " + numeroIteration);
			this.lblTemperature.setText(String.format("Temperature : %.4f", temperature));
			this.lblCout.setText(String.format("Meilleur coût : %.2f", meilleurCout));
			this.lblVehicules.setText("Vehicules : " + nombreTournees);
			this.ajouterLigneJournal(String.format("iter=%-8d T=%-12.4f courante=%-10.2f meilleure=%-10.2f tournees=%d",
													numeroIteration, temperature, coutCourant, meilleurCout, nombreTournees));
		});
	}

	public void majTerminaison(double coutFinal, String detailsSolution, long dureeMillisecondes, 
							double coutOptimal, int nombreVehicules, int nombreClients)
	{
		SwingUtilities.invokeLater(() ->
		{
			this.lblTempsDuree.setText(String.format("Temps : %d ms (%.2f s)", dureeMillisecondes, dureeMillisecondes / 1000.0));
			this.definirEtatExecution(false);
			this.ajouterLigneJournal("═══════════════════════════════════════════════════════=");
			this.ajouterLigneJournal(String.format("TERMINE en %d ms — Coût : %.2f (optimal : %.2f)", dureeMillisecondes, coutFinal, coutOptimal));
			this.ajouterLigneJournal(String.format("Vehicules utilises : %d", nombreVehicules));
			this.ajouterLigneJournal(String.format("Clients servis : %d", nombreClients));
			this.ajouterLigneJournal(String.format("Coût total de la solution : %.2f", coutFinal));
			this.ajouterLigneJournal("─────────────────────────────────────────────────────────");
			this.ajouterLigneJournal(detailsSolution);
		});
	}

	public void majGraphique(double[] coordonneesX, double[] coordonneesY, List<List<Integer>> listeTournees, int nombreNoeuds)
	{
		SwingUtilities.invokeLater(() -> this.panelGraphique.definirDonnees(coordonneesX, coordonneesY, listeTournees, nombreNoeuds));
	}
}