@echo off
cd /d "%~dp0"
java -jar "Othello.jar"
if errorlevel 1 (
    echo.
    echo Avvio non riuscito. Serve Java 8 o successivo installato.
    echo Se Java e' installato, controlla il messaggio qui sopra.
    pause
)
