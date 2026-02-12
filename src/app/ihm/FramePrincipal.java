package app.ihm;

import app.Controleur;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class FramePrincipal extends JFrame
{
	private PanelPrincipal    panelPrincipal;
	private PanelRecuitSimule panelRecuitSimule;

	private JTabbedPane       tabbedPane;

	public FramePrincipal(Controleur ctrl)
	{
		this.setTitle("SAE-6.01 - CPLEX et Recuit Simulé - Groupe 5");
		this.setSize(1500, 850);
		this.setLocationRelativeTo(null);
		this.setResizable(false);

		this.panelPrincipal    = new PanelPrincipal   (ctrl);
		this.panelRecuitSimule = new PanelRecuitSimule(ctrl);

		this.tabbedPane = new JTabbedPane();

		this.tabbedPane.addTab("Générateur .dat", this.panelPrincipal   );
		this.tabbedPane.addTab("Recuit Simulé"  , this.panelRecuitSimule);

		this.add(this.tabbedPane);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public PanelRecuitSimule getPanelRecuit()
	{
		return this.panelRecuitSimule;
	}
}