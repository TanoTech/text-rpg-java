# Text-RPG-Game in Java

Ho sviluppato un semplicissimo gioco con funzionalità base da terminale,

abbiamo la possibilità di creare nuovi personaggi, salvare le partite, accedere ad uno shop per vendere o comprare equipaggiamento,

possiamo livellare e guadagnare gold tramite missioni random.

Ho utilizzato un database SQLite per gestire i personaggi.

Il codice utilizza repository per la comunicazione con il database e service dove necessario.

Lo shop genera in base al livello del personaggio giocante vari pezzi di armatura e armi in modo da avere ad ogni accesso set di equipaggiamento sempre diversi.

L'utente può creare nuovi personaggi, scegliere la classe e ho implementato i controlli per gli input testuali in modo da non creare duplicati di nomi.

Le feature sono abbastanza limitate all'economia, alle missioni e al salvare/cancellare/caricare/creare nuove partite per nuovi personaggi.

Ho seguito una struttura folder e naming utilizzando le best pratices attuali, utilizzando Maven per la gestione delle dipendenze, e dividendo all'interno di src la parte di codice e di testing.

## 1. Tecnlogie usate

    Collections Framework: Ampio uso di EnumMap, ArrayList, LinkedHashMap (es. GameMenu, gestione equipaggiamento).
 
    Generics: Implementati in repository (CharacterRepository), servizi (GameService), e iteratori (MissionIterator).

    Java I/O: SQLiteCharacterRepository: Salvataggio su database SQLite tramite JDBC.

    GameLogger: Scrittura su file di log (FileHandler).

    Logging: GameLogger: Sistema di logging personalizzato con formattazione e livelli (INFO/WARN/ERROR).

    JUnit Testing e Mockito.

## 2. Design e Architettura

![Grafico](https://i.ibb.co/LzVxLgk6/deepseek-mermaid-20250628-51d5d5.png)

     Singleton: GameLogger, ServiceFactory (gestione istanze uniche).
     
     Factory: CharacterType (creazione personaggi), ServiceFactory (generazione servizi).

     Repository: CharacterRepository + SQLiteCharacterRepository (astrazione DB).

     Iterator: MissionCollection (navigazione missioni).

     Composite: EquipmentComponent (gerarchie equipaggiamento).

     Strategy: ExceptionShield (gestione errori personalizzabile).
     
     Observer: Logging centralizzato (reazione a eventi).

## 3. Struttura Modulare

     Il codice è organizzato in package funzionali: character, equipment, missions, service, repository, exceptions, mantenendo alta coesione e basso accoppiamento.

## 4. Principi SOLID

      Single Responsibility - Ogni classe ha una responsabilità specifica.

      Open/Closed - Estensibile tramite interfacce (es. CharacterRepository).

      Dependency Inversion - Dipendenze da astrazioni, non implementazioni concrete.

      Interface Segregation - Interfacce specifiche per ogni servizio.

## 5. Sicurezza

Validazione Input: Regex per nomi personaggio.

Separazione Errori:

      Messaggi utente comprensibili

      Dettagli tecnici solo nei log 

      Nessun dato sensibile nei log
