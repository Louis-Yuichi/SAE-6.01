@echo off

REM Compilation
javac -d bin "src\app\*.java" "src\app\ihm\*.java" "src\app\metier\*.java"

REM Exécution
java --enable-preview -cp "bin" app.Controleur