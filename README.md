# SAE-6.01 - Evolution d'une Application Existante

## Objectifs
- Développer une application d’optimisation et d’aide à la décision
- Utiliser les acquis de la ressource Méthodes d’Optimisation
- Formuler mathématiquement le problème
- Résoudre le problème avec Cplex
- Résoudre le problème avec la métaheuristique recuit simulé

## Prérequis
- Java JDK 25 ou supérieur
- IBM CPLEX Optimization Studio

## Structure du projet
```
SAE-6.01/
├── src/app/            # Code source Java
│   ├── Controleur.java
│   ├── ihm/            # Interface graphique
│   └── metier/         # Logique métier
├── bin/app/            # Classes compilées
├── cplex/              # Modèles CPLEX (.mod) & (.dat)
└── data/               # Fichiers de données (.txt)
```

## Lancement de l'application

### Linux
```bash
chmod +x setup.sh
./setup.sh
```

### Windows
```cmd
setup.bat
```

## Fichiers de données
Les fichiers de données (format `.txt`) sont placés dans `src/app/data/`.

## Modèles CPLEX
Les modèles d'optimisation (.mod) & (.dat) sont dans le dossier `cplex/`.

Pour exécuter un modèle :
1. Ouvrir IBM CPLEX Optimization Studio
2. Charger le fichier `.mod` & `.dat`
3. Configurer les données d'entrée
4. Lancer l'optimisation
