package rpg.validation;

import rpg.exceptions.ValidationException;
import rpg.logging.GameLogger;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class InputValidator {
    private static final GameLogger logger = GameLogger.getInstance();

    public static boolean isValid(String input, String pattern) {
        if (input == null) {
            return false;
        }

        try {
            Pattern compiledPattern = Pattern.compile(pattern);
            boolean isValid = compiledPattern.matcher(input).matches();

            if (!isValid) {
                logger.debug("Input validation failed: '" + input + "' doesn't match pattern '" + pattern + "'");
            }

            return isValid;
        } catch (PatternSyntaxException e) {
            logger.error("Invalid regex pattern: " + pattern, e);
            return false;
        }
    }

    public static void validateInput(String input, String pattern, String errorMessage) throws ValidationException {
        if (!isValid(input, pattern)) {
            throw new ValidationException(errorMessage);
        }
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        String sanitized = input.trim()
                .replaceAll("[';\"\\\\]", "")
                .replaceAll("--", "")
                .replaceAll("/\\*.*\\*/", "");

        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }

        return sanitized;
    }

    public static boolean isValidCharacterName(String name) {
        return isValid(name, "^[a-zA-Z][a-zA-Z0-9_]{2,19}$");
    }

    public static boolean isValidMenuChoice(String choice, int maxOption) {
        return isValid(choice, "[1-" + maxOption + "]");
    }

    public static boolean isPositiveInteger(String input) {
        return isValid(input, "^[1-9][0-9]*$");
    }

    public static boolean isNonNegativeInteger(String input) {
        return isValid(input, "^[0-9]+$");
    }
}