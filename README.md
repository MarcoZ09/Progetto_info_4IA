# Othello · Progetto di Informatica 4IA

Una versione desktop di Othello realizzata in Java con Swing e AWT. Puoi sfidare un compagno sullo stesso computer, giocare contro l’IA oppure allenarti con Othellity, la mascotte che ti spiega le mosse.

## Avvio rapido su Windows

1. Dalla pagina della repository scegli **Code → Download ZIP**.
2. Estrai tutto l’archivio e apri la cartella che contiene `Othello.jar`.
3. Con **Java 8 o successivo** installato, fai doppio clic su **`AVVIA.bat`**.
4. Nel menu premi **Gioca** e scegli la modalità.

Per avviare il gioco dal Prompt dei comandi, apri la cartella che contiene il JAR in Esplora file, scrivi `cmd` nella barra degli indirizzi e premi Invio. Poi esegui

```bat
java -jar Othello.jar
```

## Modalità di gioco

| Modalità | Come si gioca |
| --- | --- |
| Contro l’IA | Giochi con il Nero. Al livello **Facile** il computer sceglie una mossa legale a caso. Al **Medio** cerca la mossa che gira più pedine. Al **Difficile** valuta anche la posizione sulla scacchiera e dà priorità agli angoli. |
| Multigiocatore locale | Due giocatori si alternano sullo stesso computer. Il tabellone mostra il turno e il numero di pedine per colore. |
| Allenamento | Tu giochi con il Nero. Othellity indica una casella in oro, spiega la mossa nel fumetto e commenta la risposta del Bianco. |

![Othellity, la mascotte della modalità Allenamento](Immagini/Othellity-Happy.gif)

## Come ragiona il bot in Allenamento

Il bot scorre le 64 caselle con due cicli `for`. Per ogni mossa legale parte da un punto per ogni pedina che può girare. Con tre condizioni controlla la posizione e applica il primo caso valido.

| Posizione | Variazione del punteggio |
| --- | --- |
| Angolo | +100 |
| Casella accanto a un angolo ancora vuoto, anche in diagonale | −50 |
| Altra casella sul bordo | +10 |
| Altra casella interna | 0 |

Esempio: girare una pedina in A1 vale 101 punti. Girarne tre al centro vale 3. Il bot sceglie A1 perché una pedina nell’angolo resta del suo colore per tutta la partita. A parità di punti sceglie la prima casella trovata. Othellity usa lo stesso calcolo per suggerirti una mossa.

## Impostazioni e cronologia

Dal menu principale apri **Impostazioni** per scegliere se far apparire il testo parola per parola e se usare caratteri più grandi nel fumetto. Le scelte valgono dal prossimo allenamento e vengono conservate alla chiusura del gioco.

In Allenamento la casella in testa al fumetto indica la mossa che Othellity sta commentando. Se scegli una mossa che ottiene gli stessi punti di quella suggerita, vedrai **Buona alternativa**. Durante l’animazione puoi premere **Leggi subito** per leggere il commento completo.

In **Cronologia partite** trovi le partite concluse in tutte e tre le modalità, dalla più recente, con data, modalità, esito e punteggio. Se esci a metà, la partita non viene registrata. I file `partite.txt` e `impostazioni.txt` si trovano nella cartella `Othello4IA` del tuo profilo utente.

## Le regole in breve

Il Nero comincia. Clicca su una casella vuota in modo da racchiudere una o più pedine avversarie tra quella appena posata e una tua già presente. Le pedine racchiuse cambiano colore in tutte le direzioni valide, anche in diagonale.

Se un giocatore non ha mosse legali, il turno passa all’altro. Quando nessuno dei due può più muovere, vince chi ha più pedine. A parità di pedine la partita termina in pareggio.

## Modificare il progetto

Per ricompilare serve un **JDK** con i comandi `javac` e `jar` disponibili nel terminale. Dopo aver modificato i sorgenti, esegui **`COMPILA.bat`**. Lo script compila i file Java nella cartella `classi` e ricrea `Othello.jar` includendo le immagini. Apri **`AVVIA.bat`** per provare la nuova versione.

| File | Cosa contiene |
| --- | --- |
| [MainMenu.java](MainMenu.java) e [ModalitaGioco.java](ModalitaGioco.java) | Menu iniziale, selezione della modalità e della difficoltà. |
| [OthelloGame.java](OthelloGame.java) | Scacchiera, mosse legali, cambio di turno e risultato della partita. |
| [DiffAI.java](DiffAI.java) | Criteri con cui il computer sceglie una mossa nei tre livelli. |
| [Allenamento.java](Allenamento.java) | Partita guidata, suggerimenti e fumetti di Othellity. |
| [Archivio.java](Archivio.java) | Impostazioni, risultati e cronologia delle partite. |
| [Stile.java](Stile.java) e [Immagini](Immagini/) | Aspetto dell’interfaccia e risorse grafiche. |

## Gruppo di lavoro

La tabella riporta la divisione dei compiti del gruppo.

| Membro | Compiti assegnati |
| --- | --- |
| Marco Zinant | Coordinamento, menu iniziale, scelta della modalità e logica della partita contro l’IA. |
| Riccardo Mazza | Cronologia delle partite, messaggi di gioco, impostazioni, avvio su Windows e allenamento. |
| Samuel Michelini | Livelli di difficoltà dell’IA, interfaccia della partita e gestione dei turni in multigiocatore. |

[Leggi la relazione del progetto (PDF)](Relazione_Progetto_Othello.pdf)
