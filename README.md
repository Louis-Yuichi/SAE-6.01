# SAE-6.01 - Résolution du VRP (Vehicle Routing Problem)

## Description du Projet
Application d'optimisation et d'aide à la décision pour résoudre le problème de tournées de véhicules (VRP - Vehicle Routing Problem). Le projet propose deux approches de résolution :
- **Méthode exacte** : Utilisation de CPLEX pour obtenir la solution optimale
- **Métaheuristique** : Algorithme de recuit simulé pour des solutions approchées rapides

## Objectifs Pédagogiques
- Développer une application d'optimisation et d'aide à la décision
- Utiliser les acquis de la ressource Méthodes d'Optimisation
- Formuler mathématiquement le problème VRP
- Résoudre le problème avec IBM CPLEX (solution exacte)
- Implémenter la métaheuristique du recuit simulé
- Comparer les performances des deux approches

## Prérequis
- **Java JDK 25** ou supérieur
- **IBM CPLEX Optimization Studio** (pour la résolution exacte)
- Un terminal Bash (Linux/MacOS) ou Command Prompt (Windows)

## Structure du Projet
```
SAE-6.01/
├── src/app/                      # Code source Java
│   ├── Controleur.java           # Contrôleur principal (MVC)
│   ├── data/
│   │   └── tai75a.txt            # Fichier de données d'instance VRP
│   ├── ihm/                      # Interface Homme-Machine
│   │   ├── FramePrincipal.java   # Fenêtre principale
│   │   ├── PanelPrincipal.java   # Panel principal
│   │   ├── PanelRecuitSimule.java# Panel de configuration recuit simulé
│   │   └── PanelGraphiqueTournees.java # Visualisation graphique
│   └── metier/                   # Logique métier
│       ├── VRPData.java          # Données du problème VRP
│       ├── VRPSolution.java      # Structure de solution
│       ├── RecuitSimule.java     # Implémentation recuit simulé
│       └── Convertion.java       # Conversion de formats
├── bin/app/                      # Classes compilées
├── cplex/                        # Modèles CPLEX
│   ├── SAE-6.01.mod              # Modèle d'optimisation
│   ├── SAE-6.01.dat              # Fichier de données
│   └── SAE-6.01-generee.dat      # Fichier de données généré
├── setup.sh                      # Script de compilation/lancement (Linux)
├── setup.bat                     # Script de compilation/lancement (Windows)
├── LICENSE                       # Licence du projet
└── README.md                     # Ce fichier
```

## Installation et Lancement

### Linux / MacOS
```bash
# Rendre le script exécutable
chmod +x setup.sh

# Compiler et lancer l'application
./setup.sh
```

### Windows
```cmd
# Lancer le script batch
setup.bat
```

### Compilation manuelle
```bash
# Compilation
javac -encoding UTF-8 -d bin src/app/*.java src/app/ihm/*.java src/app/metier/*.java

# Exécution
java -cp bin app.Controleur
```

## Fonctionnalités de l'Application

### 1. Chargement des Données
- Chargement de fichiers d'instances VRP (format `.txt`)
- Conversion automatique vers le format CPLEX (`.dat`)
- Visualisation des données : nombre de clients, capacité des véhicules, demandes

### 2. Résolution par Recuit Simulé
L'algorithme de recuit simulé permet de trouver des solutions approchées avec les paramètres configurables :
- **Température initiale** : Point de départ de l'algorithme
- **Température minimale** : Critère d'arrêt
- **Coefficient de refroidissement** : Vitesse de décroissance de la température
- **Itérations par palier** : Nombre d'itérations à température constante
- **Itérations max sans amélioration** : Critère d'arrêt secondaire
- **Nombre max de véhicules** : Contrainte sur la flotte
- **Choix du voisin** : Meilleur voisin ou voisin aléatoire

### 3. Visualisation Graphique
- Affichage des tournées des véhicules
- Représentation graphique des clients et du dépôt
- Visualisation de l'évolution de la solution pendant l'optimisation

### 4. Export vers CPLEX
- Génération automatique de fichiers `.dat` compatibles CPLEX
- Permet la comparaison avec la solution optimale

## Format des Fichiers de Données

Les fichiers d'instance VRP (`.txt`) suivent le format :
```
<nombre_clients> <distance_optimale> <capacite_vehicule>
<coord_x_depot> <coord_y_depot> <demande_depot>
<coord_x_client1> <coord_y_client1> <demande_client1>
...
```

## Résolution avec CPLEX

Pour obtenir la solution optimale avec CPLEX :

1. Ouvrir **IBM CPLEX Optimization Studio**
2. Créer un nouveau projet OPL
3. Importer les fichiers :
   - Modèle : `cplex/SAE-6.01.mod`
   - Données : `cplex/SAE-6.01.dat` ou `cplex/SAE-6.01-generee.dat`
4. Exécuter l'optimisation
5. Comparer avec les résultats du recuit simulé

## Architecture Logicielle

L'application suit le pattern **MVC (Modèle-Vue-Contrôleur)** :
- **Modèle** : Classes dans `metier/` (VRPData, VRPSolution, RecuitSimule)
- **Vue** : Classes dans `ihm/` (FramePrincipal, PanelPrincipal, etc.)
- **Contrôleur** : Classe `Controleur.java`