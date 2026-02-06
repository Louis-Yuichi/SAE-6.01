package app.ihm;

import app.Controleur;

import java.io.File;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class PanelPrincipal extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private JLabel     lblFichierSource;
	private JTextField txtFichierSource;
	private JButton    btnParcourirSource;

	private JLabel     lblFichierDest;
	private JTextField txtFichierDest;
	private JButton    btnParcourirDest;

	private JButton    btnGenerer;
	private JButton    btnCharger;

	private JTextArea  txtAffichageSource;
	private JTextArea  txtAffichageModele;
	private JTextArea  txtInfos;

	public PanelPrincipal(Controleur ctrl)
	{
		this.ctrl = ctrl;

		// Initialisation des composants
		this.lblFichierSource    = new JLabel("Fichier source (.txt) :");
		this.txtFichierSource    = new JTextField(30);
		this.btnParcourirSource  = new JButton("Parcourir...");

		this.lblFichierDest      = new JLabel("Fichier destination (.dat) :");
		this.txtFichierDest      = new JTextField(30);
		this.txtFichierDest.setText("cplex/SAE-6.01-generee.dat");
		this.btnParcourirDest    = new JButton("Parcourir...");

		this.btnCharger          = new JButton("Charger fichier");
		this.btnGenerer          = new JButton("Générer .dat");

		// Zones d'affichage
		this.txtAffichageSource = new JTextArea(15, 40);
		this.txtAffichageSource.setEditable(false);
		this.txtAffichageSource.setLineWrap(true);
		this.txtAffichageSource.setWrapStyleWord(true);

		this.txtAffichageModele = new JTextArea(15, 40);
		this.txtAffichageModele.setEditable(false);
		this.txtAffichageModele.setLineWrap(true);
		this.txtAffichageModele.setWrapStyleWord(true);

		this.txtInfos = new JTextArea(5, 40);
		this.txtInfos.setEditable(false);
		this.txtInfos.setLineWrap(true);

		// Mise en place du layout principal
		this.setLayout(new BorderLayout(10, 10));
		this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Panel gauche avec onglets
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Fichier source", new JScrollPane(this.txtAffichageSource));
		tabbedPane.addTab("Modèle mathématique", new JScrollPane(this.txtAffichageModele));
		tabbedPane.addTab("Infos", new JScrollPane(this.txtInfos));

		// Panel droit avec contrôles
		JPanel panelDroit = new JPanel();
		panelDroit.setLayout(new GridLayout(8, 1, 5, 10));
		panelDroit.setPreferredSize(new Dimension(250, 400));

		panelDroit.add(new JLabel("--- SÉLECTION FICHIERS ---"));
		panelDroit.add(this.lblFichierSource);
		panelDroit.add(this.txtFichierSource);
		panelDroit.add(this.btnParcourirSource);

		panelDroit.add(this.lblFichierDest);
		panelDroit.add(this.txtFichierDest);
		panelDroit.add(this.btnParcourirDest);

		JPanel panelBoutons = new JPanel();
		panelBoutons.setLayout(new GridLayout(2, 1, 5, 5));
		panelBoutons.add(this.btnCharger);
		panelBoutons.add(this.btnGenerer);

		// Panel centre avec séparateur
		JPanel panelCentre = new JPanel(new BorderLayout(10, 10));
		panelCentre.add(tabbedPane, BorderLayout.CENTER);
		panelCentre.add(panelBoutons, BorderLayout.SOUTH);

		// Ajout des panels
		this.add(panelCentre, BorderLayout.CENTER);
		this.add(panelDroit, BorderLayout.EAST);

		// Listeners
		this.btnParcourirSource.addActionListener(this);
		this.btnParcourirDest.addActionListener(this);
		this.btnCharger.addActionListener(this);
		this.btnGenerer.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnParcourirSource)
		{
			String fichier = this.selectionnerFichier("Sélectionner le fichier source");
			if (fichier != null)
			{
				this.txtFichierSource.setText(fichier);
			}
		}
		else if (e.getSource() == this.btnParcourirDest)
		{
			String fichier = this.selectionnerFichier("Sélectionner le fichier destination");
			if (fichier != null)
			{
				this.txtFichierDest.setText(fichier);
			}
		}
		else if (e.getSource() == this.btnCharger)
		{
			String fichierSource = this.txtFichierSource.getText();
			if (fichierSource.isEmpty())
			{
				this.txtAffichageSource.setText("Veuillez sélectionner un fichier source !");
				return;
			}

			// Charger le contenu du fichier source
			String contenu = this.ctrl.lireFichierContenu(fichierSource);
			this.txtAffichageSource.setText(contenu);

			// Charger les infos
			String infos = this.ctrl.getInfos();
			this.txtInfos.setText(infos);

			// Charger le modèle mathématique
			this.chargerModele();
		}
		else if (e.getSource() == this.btnGenerer)
		{
			String fichierSource = this.txtFichierSource.getText();
			String fichierDest   = this.txtFichierDest.getText();

			if (fichierSource.isEmpty() || fichierDest.isEmpty())
			{
				this.txtInfos.setText("Erreur : Veuillez sélectionner les fichiers !");
				return;
			}

			try
			{
				this.ctrl.genererFichier(fichierSource, fichierDest);
				this.txtInfos.setText("✓ Fichier .dat généré avec succès !\nEmplacement : " + fichierDest);
			}
			catch (Exception ex)
			{
				this.txtInfos.setText("✗ Erreur : " + ex.getMessage());
			}
		}
	}

	private void chargerModele()
	{
		try
		{
			StringBuilder modele = new StringBuilder();
			Scanner sc = new Scanner(new File("cplex/SAE-6.01.mod"));
			while (sc.hasNextLine())
			{
				modele.append(sc.nextLine()).append("\n");
			}
			sc.close();
			this.txtAffichageModele.setText(modele.toString());
		}
		catch (Exception e)
		{
			this.txtAffichageModele.setText("Erreur lors de la lecture du modèle : " + e.getMessage());
		}
	}

	private String selectionnerFichier(String titre)
	{
		Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

		FileDialog dialogueFichier = new FileDialog(parent, titre, FileDialog.LOAD);

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