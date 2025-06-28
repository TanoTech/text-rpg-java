# Text-RPG-Game in Java

## 1. Tecnlogie usate

    Collections Framework: Ampio uso di EnumMap, ArrayList, LinkedHashMap (es. GameMenu, gestione equipaggiamento).
 
    Generics: Implementati in repository (CharacterRepository), servizi (GameService), e iteratori (MissionIterator).

    Java I/O: SQLiteCharacterRepository: Salvataggio su database SQLite tramite JDBC.

    GameLogger: Scrittura su file di log (FileHandler).

    Logging: GameLogger: Sistema di logging personalizzato con formattazione e livelli (INFO/WARN/ERROR).

    JUnit Testing e Mockito.

## 2. Design e Architettura

     Singleton: GameLogger, ServiceFactory (gestione istanze uniche)
     
     Factory: CharacterType (creazione personaggi), ServiceFactory (generazione servizi)

     Repository: CharacterRepository + SQLiteCharacterRepository (astrazione DB)

     Iterator: MissionCollection (navigazione missioni)

     Composite: EquipmentComponent (gerarchie equipaggiamento)

     Strategy: ExceptionShield (gestione errori personalizzabile)
     
     Observer: Logging centralizzato (reazione a eventi)
