import rpg.core.GameEngine;
import rpg.exceptions.GameException;
import rpg.logging.GameLogger;

public class Main {
    private static final GameLogger logger = GameLogger.getInstance();

    public static void main(String[] args) {
        logger.info("Starting RPG Game");

        try {
            GameEngine engine = new GameEngine();
            engine.start();
        } catch (GameException e) {
            logger.error("Fatal game error: " + e.getMessage());
            System.out.println("Game encountered a fatal error. Please check logs.");
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage());
            System.out.println("An unexpected error occurred.");
        }
    }
}