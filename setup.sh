#!/bin/bash

# Création du répertoire bin
if [ ! -d bin ]; then
    mkdir -p bin
fi

echo "Compilation des fichiers Java..."
javac -encoding UTF-8 -d bin src/app/*.java src/app/ihm/*.java src/app/metier/*.java

echo "Lancement de l'application..."
java --enable-preview -cp bin app.Controleur