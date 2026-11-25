@echo off
cd /d "%~dp0"
if not exist "Othello.jar" (
    echo Othello.jar non trovato. Estrai tutto lo ZIP e riapri AVVIA.bat.
    pause
    exit /b 1
)
java -jar "Othello.jar"
if errorlevel 1 (
    echo.
    echo Avvio non riuscito. Serve Java 8 o successivo installato.
    echo Se Java e' installato, controlla il messaggio qui sopra.
    pause
    exit /b 1
)
