# Text-RPG-Game in Java

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

Ho sviluppato un gioco con funzionalità base da terminale,

abbiamo la possibilità di creare nuovi personaggi, salvare le partite, accedere ad uno shop per vendere o comprare equipaggiamento,

possiamo livellare e guadagnare gold tramite missioni random.

Ho utilizzato un database SQLite per gestire i personaggi.

Il codice utilizza repository per la comunicazione con il database e service dove necessario.

Lo shop genera in base al livello del personaggio giocante vari pezzi di armatura e armi in modo da avere ad ogni accesso set di equipaggiamento sempre diversi.

L'utente può creare nuovi personaggi, scegliere la classe e ho implementato i controlli per gli input testuali in modo da non creare duplicati di nomi.

Ho seguito una struttura folder e naming utilizzando le best pratices attuali, utilizzando Maven per la gestione delle dipendenze, e dividendo all'interno di src la parte di codice e di testing.

<table align="center">
  <tr>
    <td>
    ![gif](https://i.pinimg.com/originals/40/ba/c2/40bac21458c77a177687847ba2f95ffa.gif)
        </td>
  </tr>
</table>

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

```bash
src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── rpg
│   │   │           ├── Main.java
│   │   │           ├── character
│   │   │           │   ├── Character.java
│   │   │           │   └── CharacterType.java
│   │   │           ├── core
│   │   │           │   └── GameEngine.java
│   │   │           ├── equipment
│   │   │           │   ├── Equipment.java
│   │   │           │   ├── EquipmentComponent.java
│   │   │           │   ├── EquipmentSet.java
│   │   │           │   ├── EquipmentSlot.java
│   │   │           │   ├── SingleEquipment.java
│   │   │           │   └── items
│   │   │           │       ├── Armor.java
│   │   │           │       └── Weapon.java
│   │   │           ├── exceptions
│   │   │           │   ├── DatabaseException.java
│   │   │           │   ├── ExceptionShield.java
│   │   │           │   ├── GameException.java
│   │   │           │   ├── ShieldedFunction.java
│   │   │           │   ├── ShieldedOperation.java
│   │   │           │   └── ValidationException.java
│   │   │           ├── logging
│   │   │           │   └── GameLogger.java
│   │   │           ├── missions
│   │   │           │   ├── Mission.java
│   │   │           │   ├── MissionCollection.java
│   │   │           │   ├── MissionManager.java
│   │   │           │   └── MissionType.java
│   │   │           ├── repository
│   │   │           │   ├── CharacterRepository.java
│   │   │           │   └── impl
│   │   │           │       └── SQLiteCharacterRepository.java
│   │   │           ├── service
│   │   │           │   ├── CharacterService.java
│   │   │           │   ├── GameService.java
│   │   │           │   ├── ServiceFactory.java
│   │   │           │   ├── ShopService.java
│   │   │           │   └── impl
│   │   │           │       ├── CharacterServiceImpl.java
│   │   │           │       ├── GameServiceImpl.java
│   │   │           │       └── ShopServiceImpl.java
│   │   │           ├── ui
│   │   │           │   └── GameMenu.java
│   │   │           └── validation
│   │   │               └── InputValidator.java
│   │   └── resources
│   │       └── sqlite
│   │           └── rpg_game.db
│   └── test
│       └── java
│           └── com
│               └── rpg
│                   ├── character
│                   │   └── CharacterTest.java
│                   ├── missions
│                   │   └── MissionTest.java
│                   └── service
│                       ├── CharacterTest.java
│                       ├── GameTest.java
│                       └── ShopTest.java
```

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

## 6. Clonazione e installazione

**Prerequisiti**  

- Java 17+
- Maven  
- SQLite  
- Windows: Chocolatey  
- macOS/Linux: Homebrew

### 1. Clonazione repository

```bash
git clone https://github.com/TanoTech/text-rpg-java.git
cd text-rpg-java
```

Utilizzando un gestore di pacchetti relativo al sistema operativo installare i prerequisiti:

- Windows:

```powershell
choco install openjdk17 maven sqlite
```

- macOS/Linux

```bash
brew install openjdk@17 maven sqlite
```

- Build and run:

```bash
mvn clean install
```

```bash
mvn exec:java
```

- Test:

```bash
mvn clean test || mvn test
```
