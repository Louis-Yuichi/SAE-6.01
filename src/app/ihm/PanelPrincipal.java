package app.ihm;

import app.Controleur;

import java.io.File;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

	private JButton    btnAnalyser;

	public PanelPrincipal(Controleur ctrl)
	{
		this.ctrl = ctrl;

		// Initialisation des composants
		this.lblFichierSource    = new JLabel("Fichier source (.txt) :");
		this.txtFichierSource    = new JTextField(40);
		this.btnParcourirSource  = new JButton("Parcourir...");

		this.lblFichierDest      = new JLabel("Fichier destination (.dat) :");
		this.txtFichierDest      = new JTextField(40);
		this.txtFichierDest.setText("cplex/SAE-6.01-generee.dat");
		this.btnParcourirDest    = new JButton("Parcourir...");

		this.btnAnalyser         = new JButton("Générer le fichier .dat");

		// Mise en place du layout
		this.setLayout(new BorderLayout(10, 10));
		this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Panel pour les champs
		JPanel panelChamps = new JPanel(new GridLayout(2, 3, 10, 15));

		panelChamps.add(this.lblFichierSource);
		panelChamps.add(this.txtFichierSource);
		panelChamps.add(this.btnParcourirSource);

		panelChamps.add(this.lblFichierDest);
		panelChamps.add(this.txtFichierDest);
		panelChamps.add(this.btnParcourirDest);

		// Panel pour le bouton
		JPanel panelBouton = new JPanel();
		panelBouton.add(this.btnAnalyser);

		// Ajout des panels
		this.add(panelChamps, BorderLayout.CENTER);
		this.add(panelBouton, BorderLayout.SOUTH);

		// Listeners
		this.btnParcourirSource.addActionListener(this);
		this.btnParcourirDest.addActionListener(this);
		this.btnAnalyser.addActionListener(this);
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
		else if (e.getSource() == this.btnAnalyser)
		{
			String fichierSource = this.txtFichierSource.getText();
			String fichierDest   = this.txtFichierDest.getText();

			this.ctrl.genererFichier(fichierSource, fichierDest);
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