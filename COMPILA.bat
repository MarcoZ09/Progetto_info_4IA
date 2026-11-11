@echo off
cd /d "%~dp0"
if not exist "classi" mkdir "classi"
javac -encoding UTF-8 -d "classi" *.java
if errorlevel 1 (
    echo Compilazione non riuscita. Controlla di avere il JDK installato.
    pause
    exit /b 1
)
jar cfe "Othello.jar" MainMenu -C "classi" . -C . Immagini
if errorlevel 1 (
    echo Creazione del file JAR non riuscita.
    pause
    exit /b 1
)
echo Progetto compilato. Ora apri AVVIA.bat.
pause
