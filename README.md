🏆 **RMI Quiz Master - Progetto di Sistemi Distribuiti 2026**

Un sistema di quiz multiplayer scalabile basato su Java RMI, progettato per gestire sfide in tempo reale tra più utenti sincronizzati all'interno di lobby dinamiche.

🌟 **Caratteristiche Principali**

- Architettura Containerizzata: Il server è completamente isolato tramite Docker, garantendo portabilità e facilità di deploy.

- CI/CD Pipeline: Integrazione continua tramite GitHub Actions che esegue build e test automatici ad ogni push.

- Sincronizzazione a Barriera: Logica di gioco sincronizzata; il server attende le risposte di tutti i partecipanti prima di procedere.

- Callback RMI Avanzate: Gestione bidirezionale delle notifiche dal server ai client in tempo reale.

- Bonus Velocità: Sistema di punteggio dinamico basato sul tempo di reazione del giocatore.

🛠 **Tech Stack**

- Java 21 (Amazon Corretto / Eclipse Temurin)

- Maven (Gestione dipendenze e build automation)

- Docker & Docker Compose (Containerizzazione e orchestrazione)

- Jackson Databind (Parsing JSON per il database domande)

- JUnit 5 (Unit testing per la logica delle lobby)

📂 **Struttura del Progetto**

- Server.java: Core engine; gestisce il registro RMI, le lobby e la sincronizzazione.

- Client.java: Interfaccia CLI interattiva con gestione dei thread per l'input non bloccante.

- Dockerfile: Configurazione multi-stage per generare un Fat-JAR ottimizzato.

- .github/workflows/main.yml: Pipeline per il testing automatico su GitHub.

🚀 **Come Avviare il Progetto**

1. Prerequisiti
 - Docker Desktop installato e avviato.
 - Java 17+ (se si desidera avviare il client manualmente).

2. Avvio Rapido con Docker (Consigliato per il Server)
   
Dalla root del progetto, esegui il comando per buildare e avviare il server:

**docker-compose up --build**

Il server sarà in ascolto sulla porta 1099.

3. Avvio del Client
   
Apri uno o più terminali e avvia il client tramite IntelliJ o Maven:

**mvn exec:java -Dexec.mainClass="Client"**

**Nota Tecnica**: Il client è configurato per comunicare con il server su 127.0.0.1:1099. Per le callback da Docker a Host, assicurati che il parametro java.rmi.server.hostname nel main del Client corrisponda al tuo IP locale.

📡** Note sull'Architettura di Rete**

Per permettere la comunicazione tra il container Docker (Linux) e il Client (Windows), il sistema utilizza:

1. Port Forwarding: Mapping della porta 1099 tra host e container.

2. Fixed Port RMI: L'oggetto remoto del server è esportato sulla porta 1099 fissa per evitare i blocchi dei firewall sui range di porte casuali.

3. Local IP Callbacks: Il client comunica il proprio IP locale al server per permettere l'invio delle domande tramite callback.

🧪 **Testing e Qualità**

Il progetto include una suite di test JUnit che verifica:

- Corretta creazione e univocità delle lobby.

- Gestione dei login duplicati.

- Logica di calcolo dei punteggi e bonus.

Puoi vedere l'esito degli ultimi test direttamente nel tab **Actions** di questo repository GitHub.
