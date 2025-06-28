package com.rpg.service;

import com.rpg.character.Character;
import com.rpg.exceptions.GameException;
import com.rpg.missions.Mission;
import com.rpg.missions.MissionManager;
import com.rpg.service.impl.GameServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GameService Tests")
@ExtendWith(MockitoExtension.class)
class GameTest {

    @Mock
    private Character mockCharacter;
    @Mock
    private Mission mockMission;
    @Mock
    private MissionManager mockMissionManager;

    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl();
        try {
            var f = GameServiceImpl.class.getDeclaredField("missionManager");
            f.setAccessible(true);
            f.set(gameService, mockMissionManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("generateRandomMission: success")
    void genRandomMission_success() throws GameException {
        when(mockMissionManager.generateRandomMission()).thenReturn(mockMission);
        assertSame(mockMission, gameService.generateRandomMission());
        verify(mockMissionManager).generateRandomMission();
    }

    @Test
    @DisplayName("generateRandomMission: exception")
    void genRandomMission_fail() {
        doAnswer(i -> {
            throw new GameException("fail");
        })
                .when(mockMissionManager).generateRandomMission();
        var ex = assertThrows(GameException.class,
                () -> gameService.generateRandomMission());
        assertEquals("fail", ex.getMessage());
    }

    @Test
    @DisplayName("attemptMission: success awards")
    void attemptMission_success() throws GameException {
        when(mockCharacter.getName()).thenReturn("C");
        when(mockMission.getReward()).thenReturn(200);
        when(mockMissionManager.attemptMission(mockCharacter, mockMission)).thenReturn(true);

        assertTrue(gameService.attemptMission(mockCharacter, mockMission));
        verify(mockCharacter).addGold(200);
        verify(mockCharacter).addExperience(100);
    }

    @Test
    @DisplayName("attemptMission: no rewards on failure")
    void attemptMission_failNoReward() throws GameException {
        when(mockMissionManager.attemptMission(mockCharacter, mockMission)).thenReturn(false);
        assertFalse(gameService.attemptMission(mockCharacter, mockMission));
        verify(mockCharacter, never()).addGold(anyInt());
    }

    @Test
    @DisplayName("attemptMission: exception from manager")
    void attemptMission_exception() {
        doAnswer(i -> {
            throw new GameException("err");
        })
                .when(mockMissionManager).attemptMission(mockCharacter, mockMission);

        var ex = assertThrows(GameException.class,
                () -> gameService.attemptMission(mockCharacter, mockMission));
        assertEquals("err", ex.getMessage());
    }

    @Test
    @DisplayName("attemptMission: null args")
    void attemptMission_nulls() {
        var ex1 = assertThrows(GameException.class,
                () -> gameService.attemptMission(null, mockMission));
        assertEquals("Character cannot be null", ex1.getMessage());

        var ex2 = assertThrows(GameException.class,
                () -> gameService.attemptMission(mockCharacter, null));
        assertEquals("Mission cannot be null", ex2.getMessage());
    }

    @Test
    @DisplayName("constructor: creates instance")
    void constructor_creates() {
        assertNotNull(new GameServiceImpl());
    }
}
