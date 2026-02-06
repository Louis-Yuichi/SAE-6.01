@echo off

REM Création du répertoire bin
if not exist bin mkdir bin

REM Compilation
setlocal enabledelayedexpansion
set "FILES="
for /r src %%f in (*.java) do set "FILES=!FILES! "%%f""
javac -d bin %FILES%

REM Exécution
java --enable-preview -cp "bin" app.Controleur