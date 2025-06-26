package rpg.core;

import rpg.character.Character;
import rpg.character.CharacterFactory;
import rpg.database.GameDatabase;
import rpg.exceptions.GameException;
import rpg.exceptions.ExceptionShield;
import rpg.logging.GameLogger;
import rpg.missions.MissionManager;
import rpg.shop.Shop;
import rpg.ui.GameMenu;
import rpg.validation.InputValidator;

import java.util.Scanner;

public class GameEngine {
    private static final GameLogger logger = GameLogger.getInstance();
    private final Scanner scanner;
    private final GameDatabase database;
    private final CharacterFactory characterFactory;
    private final MissionManager missionManager;
    private final Shop shop;
    private Character currentCharacter;
    private boolean running;

    public GameEngine() throws GameException {
        this.scanner = new Scanner(System.in);
        this.database = new GameDatabase();
        this.characterFactory = new CharacterFactory();
        this.missionManager = new MissionManager();
        this.shop = new Shop();
        this.running = false;

        logger.info("Game Engine initialized");
    }

    public void start() throws GameException {
        ExceptionShield.execute(() -> {
            initializeGame();
            mainGameLoop();
        }, "Error starting game");
    }

    private void initializeGame() throws GameException {
        System.out.println("=== Welcome to TanoTech RPG===");
        running = true;

        GameMenu mainMenu = new GameMenu("Main Menu");
        mainMenu.addOption("1", "New Game");
        mainMenu.addOption("2", "Load Game");
        mainMenu.addOption("3", "Delete Save");
        mainMenu.addOption("4", "Exit");

        while (running) {
            mainMenu.display();
            String choice = getValidInput("Choose an option: ", "[1-4]");

            switch (choice) {
                case "1":
                    startNewGame();
                    break;
                case "2":
                    loadGame();
                    break;
                case "3":
                    deleteSave();
                    break;
                case "4":
                    exitGame();
                    break;
            }
        }
    }

    private void mainGameLoop() throws GameException {
        if (currentCharacter == null)
            return;

        GameMenu gameMenu = new GameMenu("Game Menu");
        gameMenu.addOption("1", "View Character");
        gameMenu.addOption("2", "Start Mission");
        gameMenu.addOption("3", "Visit Shop");
        gameMenu.addOption("4", "Save Game");
        gameMenu.addOption("5", "Return to Main Menu");

        boolean inGame = true;
        while (inGame && running) {
            gameMenu.display();
            String choice = getValidInput("Choose an option: ", "[1-5]");

            switch (choice) {
                case "1":
                    displayCharacter();
                    break;
                case "2":
                    startMission();
                    break;
                case "3":
                    visitShop();
                    break;
                case "4":
                    saveGame();
                    break;
                case "5":
                    inGame = false;
                    break;
            }
        }
    }

    private void startNewGame() throws GameException {
        System.out.println("\n=== Create New Character ===");

        String name = getValidInput("Enter character name: ", "^[a-zA-Z][a-zA-Z0-9_]{2,19}$");

        System.out.println("Choose character class:");
        System.out.println("1. Warrior (High HP, Medium Attack)");
        System.out.println("2. Mage (Medium HP, High Attack)");
        System.out.println("3. Rogue (Low HP, Very High Attack)");

        String classChoice = getValidInput("Choose class: ", "[1-3]");
        String characterClass = switch (classChoice) {
            case "1" -> "Warrior";
            case "2" -> "Mage";
            case "3" -> "Rogue";
            default -> "Warrior";
        };

        currentCharacter = characterFactory.createCharacter(characterClass, name);
        logger.info("Created new character: " + name + " (" + characterClass + ")");

        System.out.println("\nCharacter created successfully!");
        mainGameLoop();
    }

    private void loadGame() throws GameException {
        var saves = database.getAllSaves();

        if (saves.isEmpty()) {
            System.out.println("No saved games found.");
            return;
        }

        System.out.println("\n=== Load Game ===");
        int index = 1;
        for (String save : saves) {
            System.out.println(index + ". " + save);
            index++;
        }

        String choice = getValidInput("Choose save (1-" + saves.size() + "): ",
                "[1-" + saves.size() + "]");
        int saveIndex = Integer.parseInt(choice) - 1;
        String saveName = saves.get(saveIndex);

        currentCharacter = database.loadCharacter(saveName);
        if (currentCharacter != null) {
            System.out.println("Game loaded successfully!");
            logger.info("Loaded character: " + saveName);
            mainGameLoop();
        } else {
            System.out.println("Failed to load game.");
        }
    }

    private void deleteSave() throws GameException {
        var saves = database.getAllSaves();

        if (saves.isEmpty()) {
            System.out.println("No saved games found.");
            return;
        }

        System.out.println("\n=== Delete Save ===");
        int index = 1;
        for (String save : saves) {
            System.out.println(index + ". " + save);
            index++;
        }

        String choice = getValidInput("Choose save to delete (1-" + saves.size() + "): ",
                "[1-" + saves.size() + "]");
        int saveIndex = Integer.parseInt(choice) - 1;
        String saveName = saves.get(saveIndex);

        String confirm = getValidInput("Are you sure you want to delete '" + saveName + "'? (y/n): ", "[yn]");
        if (confirm.equals("y")) {
            database.deleteSave(saveName);
            System.out.println("Save deleted successfully.");
            logger.info("Deleted save: " + saveName);
        }
    }

    private void displayCharacter() {
        if (currentCharacter != null) {
            System.out.println("\n" + currentCharacter.getDetailedInfo());
        }
    }

    private void startMission() throws GameException {
        if (currentCharacter == null)
            return;

        var mission = missionManager.generateRandomMission();
        System.out.println("\n=== Mission Available ===");
        System.out.println(mission.getDescription());
        System.out.println("Reward: " + mission.getReward() + " gold");
        System.out.println("Difficulty: " + mission.getDifficulty());

        String accept = getValidInput("Accept mission? (y/n): ", "[yn]");
        if (accept.equals("y")) {
            boolean success = missionManager.attemptMission(currentCharacter, mission);
            if (success) {
                System.out.println("Mission completed successfully!");
                currentCharacter.addGold(mission.getReward());
                currentCharacter.addExperience(mission.getReward() / 2);
            } else {
                System.out.println("Mission failed. Better luck next time!");
            }
        }
    }

    private void visitShop() throws GameException {
        if (currentCharacter == null)
            return;

        shop.interact(currentCharacter, scanner);
    }

    private void saveGame() throws GameException {
        if (currentCharacter == null) {
            System.out.println("No character to save.");
            return;
        }

        database.saveCharacter(currentCharacter);
        System.out.println("Game saved successfully!");
        logger.info("Saved character: " + currentCharacter.getName());
    }

    private void exitGame() {
        System.out.println("Thank you for playing!");
        running = false;
        logger.info("Game exited normally");
    }

    private String getValidInput(String prompt, String pattern) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (InputValidator.isValid(input, pattern)) {
                return input.toLowerCase();
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}