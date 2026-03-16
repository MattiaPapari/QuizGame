RMI Quiz Master - Progetto di Sistemi Distribuiti 2026
Un sistema di quiz multiplayer basato su Java RMI, che permette a più utenti di sfidarsi in tempo reale all'interno di diverse lobby.

🛠 Tecnologie Utilizzate
Java 17+: Linguaggio principale.

Java RMI: Per la comunicazione tra oggetti remoti.

Maven: Per la gestione delle dipendenze e il build.

Jackson Databind: Per il parsing del file Questions.json.

📂 Struttura del Progetto
Server.java: Gestisce la logica delle lobby, i punteggi e la sincronizzazione delle risposte.

Client.java: Interfaccia utente (CLI) e gestione delle callback per ricevere le domande.

ServerInterface.java / ClientInterface.java: Interfacce remote per il protocollo RMI.

Questions.json: Database delle domande in formato JSON.

🚀 Come Avviare il Progetto
1. Prerequisiti
Assicurati di avere Maven installato e che la porta 1099 sia libera sul tuo sistema.

2. Compilazione
Dalla cartella principale del progetto, compila il tutto con Maven:

3. Avvio del Server
Esegui la classe Server. Questo avvierà automaticamente il rmiregistry sulla porta 1099.

4. Avvio dei Client
Puoi aprire più terminali per simulare diversi giocatori.

🎮 Funzionalità Principali
Sistema di Lobby: Creazione di nuove lobby o accesso a lobby esistenti tramite ID numerico.

Sincronizzazione a Barriera: Il server attende che tutti i partecipanti attivi abbiano risposto prima di passare alla domanda successiva.

Bonus Velocità: Punti extra per chi risponde correttamente in meno di 5 secondi.

Gestione Dinamica Client: Rimozione automatica dei client disconnessi per evitare lo stallo della partita.
