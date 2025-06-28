package com.rpg.character;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import com.rpg.exceptions.GameException;
import com.rpg.logging.GameLogger;

import static org.junit.jupiter.api.Assertions.*;

class CharacterFactoryTest {

    @Test
    void createValidCharacters_andLogs() throws GameException {
        try (MockedStatic<GameLogger> mockedLoggerStatic = Mockito.mockStatic(GameLogger.class)) {
            GameLogger fakeLogger = Mockito.mock(GameLogger.class);
            mockedLoggerStatic.when(GameLogger::getInstance).thenReturn(fakeLogger);

            CharacterFactory factory = new CharacterFactory();

            Character w = factory.createCharacter("warrior", "Conan");
            assertEquals("Warrior", w.getCharacterClass());
            Character m = factory.createCharacter("mage", "Gandalf");
            assertEquals("Mage", m.getCharacterClass());
            Character r = factory.createCharacter("rogue", "Garrett");
            assertEquals("Rogue", r.getCharacterClass());

            Mockito.verify(fakeLogger, Mockito.times(3))
                    .info(Mockito.argThat(msg -> msg.contains("Created character")));
        }
    }

    @Test
    void createCharacter_invalidName_throws() {
        CharacterFactory factory = new CharacterFactory();
        GameException ex = assertThrows(GameException.class, () -> factory.createCharacter("warrior", "  "));
        assertEquals("Character name cannot be empty", ex.getMessage());
    }

    @Test
    void createCharacter_unknownType_throws() {
        CharacterFactory factory = new CharacterFactory();
        GameException ex = assertThrows(GameException.class, () -> factory.createCharacter("paladin", "Uther"));
        assertTrue(ex.getMessage().contains("Unknown character type"));
    }
}
