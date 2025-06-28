package com.rpg.service;

import com.rpg.character.Character;
import com.rpg.character.CharacterType;
import com.rpg.exceptions.DatabaseException;
import com.rpg.exceptions.GameException;
import com.rpg.repository.CharacterRepository;
import com.rpg.service.impl.CharacterServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CharacterService Tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CharacterTest {

    @Mock
    private CharacterRepository mockRepository;

    @Mock
    private Character mockCharacter;

    private CharacterServiceImpl characterService;

    @BeforeEach
    void setUp() {
        characterService = new CharacterServiceImpl(mockRepository);
    }

    @Test
    @DisplayName("createCharacter: success when name unique")
    void createCharacter_success() throws GameException {
        String type = "WARRIOR", name = "Hero";
        when(mockRepository.existsByName(name)).thenReturn(false);
        try (MockedStatic<CharacterType> mt = mockStatic(CharacterType.class)) {
            var mockType = mock(CharacterType.class);
            mt.when(() -> CharacterType.fromString(type)).thenReturn(mockType);
            when(mockType.create(name)).thenReturn(mockCharacter);

            var result = characterService.createCharacter(type, name);
            assertSame(mockCharacter, result);
            verify(mockRepository).existsByName(name);
            mt.verify(() -> CharacterType.fromString(type));
            verify(mockType).create(name);
        }
    }

    @Test
    @DisplayName("createCharacter: exception on duplicate name")
    void createCharacter_duplicate() throws DatabaseException {
        when(mockRepository.existsByName("Dup")).thenReturn(true);
        var ex = assertThrows(GameException.class,
                () -> characterService.createCharacter("MAGE", "Dup"));
        assertEquals("Character 'Dup' already exists", ex.getMessage());
    }

    @Test
    @DisplayName("saveCharacter: success")
    void saveCharacter_success() throws GameException {
        characterService.saveCharacter(mockCharacter);
        verify(mockRepository).save(mockCharacter);
    }

    @Test
    @DisplayName("saveCharacter: exception on null")
    void saveCharacter_null() {
        var ex = assertThrows(GameException.class,
                () -> characterService.saveCharacter(null));
        assertEquals("Cannot save null character", ex.getMessage());
        verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("loadCharacter: success when exists")
    void loadCharacter_success() throws GameException {
        when(mockRepository.findByName("X")).thenReturn(Optional.of(mockCharacter));
        assertSame(mockCharacter, characterService.loadCharacter("X"));
    }

    @Test
    @DisplayName("loadCharacter: exception when not found")
    void loadCharacter_notFound() throws DatabaseException {
        when(mockRepository.findByName("X")).thenReturn(Optional.empty());
        var ex = assertThrows(GameException.class,
                () -> characterService.loadCharacter("X"));
        assertEquals("Character not found: X", ex.getMessage());
    }

    @Test
    @DisplayName("getAllSaves: returns list")
    void getAllSaves() throws GameException {
        List<String> names = Arrays.asList("A", "B");
        when(mockRepository.findAllCharacterNames()).thenReturn(names);
        var result = characterService.getAllSaves();
        assertEquals(names, result);
    }

    @Test
    @DisplayName("deleteSave: success and failure")
    void deleteSave() throws GameException {
        when(mockRepository.deleteByName("Y")).thenReturn(true);
        assertTrue(characterService.deleteSave("Y"));
        when(mockRepository.deleteByName("Z")).thenReturn(false);
        assertFalse(characterService.deleteSave("Z"));
    }

    @Test
    @DisplayName("deleteSave: exception on invalid name")
    void deleteSave_invalid() {
        var ex1 = assertThrows(GameException.class,
                () -> characterService.deleteSave(null));
        assertEquals("Character name cannot be empty", ex1.getMessage());
        var ex2 = assertThrows(GameException.class,
                () -> characterService.deleteSave("  "));
        assertEquals("Character name cannot be empty", ex2.getMessage());
    }
}
