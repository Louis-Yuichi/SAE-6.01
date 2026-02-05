#!/bin/bash

echo "Compilation des fichiers Java..."
javac -encoding UTF-8 -d bin src/app/*.java src/app/ihm/*.java src/app/metier/*.java

echo "Lancement de l'application..."
java -cp bin app.Controleur