package com.rpg.exceptions;

import com.rpg.logging.GameLogger;

public class ExceptionShield {
    private static final GameLogger logger = GameLogger.getInstance();

    public static void execute(ShieldedOperation operation, String context) throws GameException {
        try {
            operation.execute();
        } catch (GameException e) {
            logger.warn("Game exception in " + context + ": " + e.getTechnicalMessage());
            throw e;
        } catch (Exception e) {
            String technicalMessage = "Unexpected error in " + context + ": " + e.getMessage();
            String userMessage = "An unexpected error occurred. Please try again.";

            logger.error(technicalMessage, e);
            throw new GameException(userMessage, technicalMessage, e);
        }
    }

    public static <T> T executeWithReturn(ShieldedFunction<T> function, String context) throws GameException {
        try {
            return function.execute();
        } catch (GameException e) {
            logger.warn("Game exception in " + context + ": " + e.getTechnicalMessage());
            throw e;
        } catch (Exception e) {
            String technicalMessage = "Unexpected error in " + context + ": " + e.getMessage();
            String userMessage = "An unexpected error occurred. Please try again.";

            logger.error(technicalMessage, e);
            throw new GameException(userMessage, technicalMessage, e);
        }
    }

    public static <T> T executeWithDefault(ShieldedFunction<T> function, T defaultValue, String context) {
        try {
            return function.execute();
        } catch (Exception e) {
            String technicalMessage = "Error in " + context + ": " + e.getMessage();
            logger.error(technicalMessage, e);
            return defaultValue;
        }
    }

    public static void executeSilently(ShieldedOperation operation, String context) {
        try {
            operation.execute();
        } catch (Exception e) {
            String technicalMessage = "Silenced error in " + context + ": " + e.getMessage();
            logger.error(technicalMessage, e);
        }
    }

    public static void executeDatabaseOperation(ShieldedOperation operation, String context) throws DatabaseException {
        try {
            operation.execute();
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            String technicalMessage = "Database error in " + context + ": " + e.getMessage();
            logger.error(technicalMessage, e);
            throw new DatabaseException(technicalMessage, e);
        }
    }

    public static <T> T executeDatabaseOperationWithReturn(ShieldedFunction<T> function, String context)
            throws DatabaseException {
        try {
            return function.execute();
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            String technicalMessage = "Database error in " + context + ": " + e.getMessage();
            logger.error(technicalMessage, e);
            throw new DatabaseException(technicalMessage, e);
        }
    }
}
