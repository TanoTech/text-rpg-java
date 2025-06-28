# Text-RPG-Game in Java

## 1. Tecnlogie usate

    Collections Framework: Ampio uso di EnumMap, ArrayList, LinkedHashMap (es. GameMenu, gestione equipaggiamento).
 
    Generics: Implementati in repository (CharacterRepository), servizi (GameService), e iteratori (MissionIterator).

    Java I/O: SQLiteCharacterRepository: Salvataggio su database SQLite tramite JDBC.

    GameLogger: Scrittura su file di log (FileHandler).

    Logging: GameLogger: Sistema di logging personalizzato con formattazione e livelli (INFO/WARN/ERROR).

    JUnit Testing e Mockito.

## 2. Design e Architettura

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
