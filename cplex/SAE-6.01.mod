/*********************************************
 * OPL 22.1.1.0 Model
 * Auteur : Groupe 5
 * Date   : 02-02-2026
 *********************************************/

/*********************************************
 *           Définition des données
 *********************************************/

// Nombre de clients
int nbClient = ...;
// Nombre de dépôts
int nbDepot = ...;
// Calcul nombre de noeuds
int nbNoeud = nbClient+nbDepot;
// Nombre de véhicules
int nbVehicule = ...;
// Distance entre les noeuds (clients ou dépôt) i et j
float distances[1..nbNoeud][1..nbNoeud] = ...;
// Demande du client i 
float demandes[1..nbClient] = ...;
// Capacité du véhicule v 
int qMax = ...;

/*********************************************
 *           Variables de décision
 *********************************************/

// xijv = 1 si le véhicule v passe directement du noeud i au noeud j, 0 sinon
dvar boolean x[1..nbNoeud][1..nbNoeud][1..nbVehicule];
// Capacité restante du véhicule v au retour au dépôt
dvar int qapresretour[1..nbVehicule];
// Nombre de véhicules utilisés
dvar int+ nbre;
// Variables auxiliaires pour éliminer les sous-tours
dvar int+ u[1..nbNoeud];

/*********************************************
 *             Fonction objectif :
 *     Minimiser la distance des tournées
 *********************************************/

dexpr float objectif = sum(i in 1..nbNoeud, j in 1..nbNoeud, v in 1..nbVehicule) distances[i][j] * x[i][j][v];
minimize objectif;

/*********************************************
 *                Contraintes
 *********************************************/

subject to
{
	// Un véhicule qui quitte le dépôt, retourne au dépôt à la fin de sa tournée
	forall (v in 1..nbVehicule)
	{
		sum(j in (nbDepot+1)..nbNoeud) x[j][1][v] == sum (j in (nbDepot+1).. nbNoeud) x[1][j][v];
		sum(j in (nbDepot+1)..nbNoeud) x[j][1][v] <= 1;
	}
	
	// Un véhicule ne peut pas faire de boucle sur un même noeud
	forall (i in 1..nbNoeud, v in 1..nbVehicule)
		x[i][i][v] == 0;
	  
	// Un client est visité exactement une et une seule fois par un véhicule
	forall (j in (nbDepot+1)..nbNoeud)
		sum(v in 1..nbVehicule, i in 1..nbNoeud) x[i][j][v] == 1;
	
	// Conservation de flux
	forall (j in 1..nbNoeud, v in 1..nbVehicule)
		sum (i in 1..nbNoeud)x[j][i][v] == sum (i in 1..nbNoeud) x[i][j][v];
	
	// Au moins un véhicule est utilisé pour la construction des tournées
	nbre == sum(j in 1..nbNoeud, v in 1..nbVehicule) x[1][j][v];
	nbre >= 1;
	
	// Elimination des sous-tours
	forall (i in (nbDepot+1)..nbNoeud, j in (nbDepot+1)..nbNoeud, v in 1..nbVehicule : i!=j)
		u[j]-u[i] >= demandes [j-nbDepot] - qMax*(1-x[i][j][v]);
	forall (i in (nbDepot+1)..nbNoeud)
	{
		demandes[i-1] <= u[i];
		u[1] <= qMax;
	}
	
	// La capacité du véhicule ne peut être dépassée
	forall (v in 1..nbVehicule)
		sum(i in 1..nbNoeud, j in (nbDepot+1)..nbNoeud) demandes[j-nbDepot]*x[i][j][v] <= qMax;

	// Calcul de la capacité restante au retour au dépôt
	forall (v in 1..nbVehicule)
		qapresretour[v] == qMax - sum(i in 1..nbNoeud, j in (nbDepot+1)..nbNoeud) demandes [j-nbDepot] * x[i][j][v];
}

/*********************************************
 *        Test de la condition d'arrêt
 *********************************************/

main
{
	var totDemande = 0;
	for (var i = 1; i <= thisOplModel.nbClient; i++)
		totDemande += thisOplModel.demandes[i];

	writeln("Demande totale des clients = ", totDemande);
	writeln("Capacité des véhicules disponible = ", thisOplModel.nbVehicule * thisOplModel.qMax);
	
	if (totDemande > thisOplModel.nbVehicule * thisOplModel.qMax)
	{
		writeln("⚠️ PROBLÈME INFAISABLE");
		writeln("Nombre de véhicules insuffisant → arrêt");
	}

	writeln("Résolution lancée…");
	thisOplModel.generate();

	if (cplex.solve())
		writeln("Solution trouvée, valeur objectif = ", cplex.getObjValue());
	else
		writeln("Aucune solution trouvée");

/*********************************************
 *           Paramètres d'affichage
 *********************************************/	
	
	// Utiliser un tableau scriptable pour suivre les noeuds visités
	var visiter = new Array(thisOplModel.nbNoeud + 1);
	for (var v = 1; v <= thisOplModel.nbVehicule; v++)
	{
		// Initialiser le tableay visiter pour chaque véhicule
		for (var j = 1; j <= thisOplModel.nbNoeud; j++) 
			visiter[j] = 0;
		var route = "Dépôt";
		var i = 1;
		while(true)
		{
			var found = 0;
			for (var j = 1; j <= thisOplModel.nbNoeud; j++)
			{
				if ((j <= thisOplModel.nbDepot || visiter [j] != 1) && thisOplModel.x[i][j][v] == 1) 
				{
					visiter[j] = 1;
					i = j;
					if (j <= thisOplModel.nbDepot)
						route += " -> Dépôt";
					else
						route += " -> C" + (j - thisOplModel.nbDepot);
					found = 1;
					break;
				}
			}
			if (found != 1) break;
		}
		if (route == "Dépôt")
			write(" ");
		else
		{
			write ("Vehicule ", v, " : ");
			writeln(route);
			write("Capacité utilisée : ", (thisOplModel.qMax - thisOplModel.qapresretour[v]));
		}
		writeln();
	}
}