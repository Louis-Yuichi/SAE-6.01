package app.ihm;

import app.Controleur;

import java.io.File;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class PanelPrincipal extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private JTextField txtSource;
	private JTextField txtDest;
	private JButton    btnParcourirSource;
	private JButton    btnParcourirDest;
	private JButton    btnCharger;
	private JButton    btnGenerer;
	private JTextPane  txtContenu;
	private JTextArea  txtPreviewDat;
	private JLabel     lblStatut;
	
	private JLabel     lblNbClients;
	private JLabel     lblQMax;
	private JLabel     lblDistanceOpt;
	private JPanel     panelStats;

	public PanelPrincipal(Controleur ctrl)
	{
		this.ctrl = ctrl;

		this.setLayout(new BorderLayout(15, 15));
		this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		this.setBackground(new Color(245, 245, 250));

		JPanel panelTitre    = this.creerPanelTitre();
		JPanel panelFichiers = this.creerPanelFichiers();
		this.panelStats      = this.creerPanelStats();
		JPanel panelCentre   = this.creerPanelCentre();
		JPanel panelBas      = this.creerPanelBas();

		JPanel panelNord = new JPanel();
		panelNord.setLayout(new BoxLayout(panelNord, BoxLayout.Y_AXIS));
		panelNord.setOpaque(false);
		panelNord.add(panelTitre);
		panelNord.add(Box.createVerticalStrut(15));
		panelNord.add(panelFichiers);
		panelNord.add(Box.createVerticalStrut(15));
		panelNord.add(this.panelStats);

		this.add(panelNord  , BorderLayout.NORTH );
		this.add(panelCentre, BorderLayout.CENTER);
		this.add(panelBas   , BorderLayout.SOUTH );

		this.btnParcourirSource.addActionListener(this);
		this.btnParcourirDest  .addActionListener(this);
		this.btnCharger        .addActionListener(this);
		this.btnGenerer        .addActionListener(this);
		
		this.panelStats.setVisible(false);
	}

	private JPanel creerPanelTitre()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(new Color(41, 128, 185));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel lblTitre = new JLabel("GENERATEUR CPLEX - SAE 6.01", SwingConstants.CENTER);
		lblTitre.setFont(new Font("Arial", Font.BOLD, 26));
		lblTitre.setForeground(Color.WHITE);

		JLabel lblSousTitre = new JLabel("Conversion de fichiers .txt vers format .dat OPL", SwingConstants.CENTER);
		lblSousTitre.setFont(new Font("Arial", Font.PLAIN, 14));
		lblSousTitre.setForeground(new Color(236, 240, 241));

		panel.add(lblTitre, BorderLayout.CENTER);
		panel.add(lblSousTitre, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel creerPanelFichiers()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 3, 15, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
			BorderFactory.createEmptyBorder(20, 20, 20, 20)
		));

		this.txtSource          = new JTextField("src/app/data/tai75a.txt", 30);
		this.txtDest            = new JTextField("cplex/SAE-6.01-generee.dat", 30);
		this.btnParcourirSource = new JButton("Parcourir...");
		this.btnParcourirDest   = new JButton("Parcourir...");

		this.txtSource.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.txtDest  .setFont(new Font("Monospaced", Font.PLAIN, 12));

		this.btnParcourirSource.setPreferredSize(new Dimension(120, 30));
		this.btnParcourirDest  .setPreferredSize(new Dimension(120, 30));
		this.btnParcourirSource.setBackground(new Color(149, 165, 166));
		this.btnParcourirDest  .setBackground(new Color(149, 165, 166));
		this.btnParcourirSource.setForeground(Color.WHITE);
		this.btnParcourirDest  .setForeground(Color.WHITE);
		this.btnParcourirSource.setFocusPainted(false);
		this.btnParcourirDest  .setFocusPainted(false);

		JLabel lblSource = new JLabel("Fichier source (.txt) :");
		JLabel lblDest   = new JLabel("Fichier destination (.dat) :");
		lblSource.setFont(new Font("Arial", Font.BOLD, 13));
		lblDest  .setFont(new Font("Arial", Font.BOLD, 13));

		panel.add(lblSource);
		panel.add(this.txtSource);
		panel.add(this.btnParcourirSource);
		panel.add(lblDest);
		panel.add(this.txtDest);
		panel.add(this.btnParcourirDest);

		return panel;
	}

	private JPanel creerPanelStats()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3, 15, 0));
		panel.setOpaque(false);

		this.lblNbClients   = new JLabel("0", SwingConstants.CENTER);
		this.lblQMax        = new JLabel("0", SwingConstants.CENTER);
		this.lblDistanceOpt = new JLabel("0.00", SwingConstants.CENTER);

		JPanel panelClients = this.creerCarteStat("CLIENTS", this.lblNbClients, new Color(52, 152, 219));
		JPanel panelQMax    = this.creerCarteStat("CAPACITE MAX", this.lblQMax, new Color(46, 204, 113));
		JPanel panelDist    = this.creerCarteStat("DISTANCE OPTIMALE", this.lblDistanceOpt, new Color(155, 89, 182));

		panel.add(panelClients);
		panel.add(panelQMax);
		panel.add(panelDist);

		return panel;
	}

	private JPanel creerCarteStat(String titre, JLabel lblValeur, Color couleur)
	{
		JPanel carte = new JPanel();
		carte.setLayout(new BorderLayout(0, 10));
		carte.setBackground(couleur);
		carte.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(couleur.darker(), 2),
			BorderFactory.createEmptyBorder(20, 15, 20, 15)
		));

		JLabel lblTitre = new JLabel(titre, SwingConstants.CENTER);
		lblTitre.setFont(new Font("Arial", Font.BOLD, 11));
		lblTitre.setForeground(Color.WHITE);

		lblValeur.setFont(new Font("Arial", Font.BOLD, 32));
		lblValeur.setForeground(Color.WHITE);

		carte.add(lblTitre, BorderLayout.NORTH);
		carte.add(lblValeur, BorderLayout.CENTER);

		return carte;
	}

	private JPanel creerPanelCentre()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

		// Onglet 1: Fichier source
		this.txtContenu = new JTextPane();
		this.txtContenu.setEditable(true);
		this.txtContenu.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.txtContenu.setBackground(new Color(250, 250, 250));

		JScrollPane scrollContenu = new JScrollPane(this.txtContenu);
		scrollContenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Onglet 2: Prévisualisation .dat
		this.txtPreviewDat = new JTextArea();
		this.txtPreviewDat.setEditable(false);
		this.txtPreviewDat.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.txtPreviewDat.setBackground(new Color(250, 250, 250));
		this.txtPreviewDat.setLineWrap(false);
		this.txtPreviewDat.setWrapStyleWord(false);

		JScrollPane scrollPreview = new JScrollPane(this.txtPreviewDat);
		scrollPreview.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		tabbedPane.addTab("Fichier source (.txt)", scrollContenu);
		tabbedPane.addTab("Previsualisation .dat", scrollPreview);

		panel.add(tabbedPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel creerPanelBas()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 10));
		panel.setOpaque(false);

		JPanel panelBoutons = new JPanel();
		panelBoutons.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		panelBoutons.setOpaque(false);

		this.btnCharger = new JButton("Charger le fichier");
		this.btnGenerer = new JButton("Generer le .dat");
		this.lblStatut  = new JLabel(" Pret a demarrer", SwingConstants.CENTER);

		this.btnCharger.setPreferredSize(new Dimension(180, 45));
		this.btnGenerer.setPreferredSize(new Dimension(180, 45));

		this.btnCharger.setFont(new Font("Arial", Font.BOLD, 14));
		this.btnGenerer.setFont(new Font("Arial", Font.BOLD, 14));
		this.lblStatut .setFont(new Font("Arial", Font.ITALIC, 13));

		this.btnCharger.setBackground(new Color(52, 152, 219));
		this.btnGenerer.setBackground(new Color(46, 204, 113));
		this.btnCharger.setForeground(Color.WHITE);
		this.btnGenerer.setForeground(Color.WHITE);
		this.btnCharger.setFocusPainted(false);
		this.btnGenerer.setFocusPainted(false);

		this.lblStatut.setForeground(new Color(52, 73, 94));
		this.lblStatut.setOpaque(true);
		this.lblStatut.setBackground(new Color(236, 240, 241));
		this.lblStatut.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		panelBoutons.add(this.btnCharger);
		panelBoutons.add(this.btnGenerer);

		panel.add(panelBoutons, BorderLayout.NORTH);
		panel.add(this.lblStatut, BorderLayout.SOUTH);

		return panel;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnParcourirSource)
		{
			String chemin = this.selectionnerFichier("Fichier source");
			if (chemin != null) this.txtSource.setText(chemin);
		}

		if (e.getSource() == this.btnParcourirDest)
		{
			String chemin = this.selectionnerFichier("Fichier destination");
			if (chemin != null) this.txtDest.setText(chemin);
		}

		if (e.getSource() == this.btnCharger)
		{
			String source = this.txtSource.getText().trim();

			if (source.isEmpty())
			{
				this.lblStatut.setText(" Veuillez selectionner un fichier source");
				this.lblStatut.setForeground(new Color(231, 76, 60));
				return;
			}

			this.lblStatut.setText(" Chargement en cours...");
			this.lblStatut.setForeground(new Color(241, 196, 15));

			try
			{
				String contenu = this.ctrl.getFichierTxt(source);
				this.txtContenu.setText(contenu);
				
				// Générer la prévisualisation du .dat
				String contenuDat = this.ctrl.getFichierDat(source);
				this.txtPreviewDat.setText(contenuDat);
				
				this.lblStatut.setText(" Fichier charge : " + new File(source).getName());
				this.lblStatut.setForeground(new Color(39, 174, 96));
				this.panelStats.setVisible(false);
			}
			catch (Exception ex)
			{
				this.lblStatut.setText(" Erreur : " + ex.getMessage());
				this.lblStatut.setForeground(new Color(231, 76, 60));
			}
		}

		if (e.getSource() == this.btnGenerer)
		{
			String source = this.txtSource.getText().trim();
			String dest   = this.txtDest  .getText().trim();

			if (source.isEmpty() || dest.isEmpty())
			{
				this.lblStatut.setText(" Veuillez completer les champs");
				this.lblStatut.setForeground(new Color(231, 76, 60));
				return;
			}

			this.lblStatut.setText(" Generation en cours...");
			this.lblStatut.setForeground(new Color(241, 196, 15));

			try
			{
				this.ctrl.chargerFichiers(source, dest);
				
				int    nbClients = this.ctrl.getNbClients();
				int    qMax      = this.ctrl.getQMax();
				double distOpt   = this.ctrl.getDistanceOptimal();

				this.lblNbClients  .setText(String.valueOf(nbClients));
				this.lblQMax       .setText(String.valueOf(qMax));
				this.lblDistanceOpt.setText(String.format("%.2f", distOpt));

				this.panelStats.setVisible(true);

				this.lblStatut.setText(" Fichier genere avec succes : " + new File(dest).getName());
				this.lblStatut.setForeground(new Color(39, 174, 96));
			}
			catch (Exception ex)
			{
				this.lblStatut.setText(" Erreur : " + ex.getMessage());
				this.lblStatut.setForeground(new Color(231, 76, 60));
			}
		}
	}

	private String selectionnerFichier(String titre)
	{
		FileDialog dialogueFichier = new FileDialog((JFrame) null, titre, FileDialog.LOAD);
		dialogueFichier.setFile("*.txt");
		dialogueFichier.setVisible(true);

		String nomFichier = dialogueFichier.getFile();
		String dossier    = dialogueFichier.getDirectory();

		if (nomFichier != null && dossier != null)
		{
			return new File(dossier, nomFichier).getAbsolutePath();
		}

		return null;
	}
}