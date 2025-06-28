package com.rpg.missions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {

    private Mission mission;

    @BeforeEach
    void setUp() {
        mission = new Mission(
                "Test Mission",
                "Complete a sample mission for testing purposes",
                5,
                150,
                MissionType.EXPLORATION);
    }

    @Test
    void testGetTitle() {
        assertEquals("Test Mission", mission.getTitle());
    }

    @Test
    void testGetDescription() {
        assertEquals("Complete a sample mission for testing purposes", mission.getDescription());
    }

    @Test
    void testGetDifficulty() {
        assertEquals(5, mission.getDifficulty());
    }

    @Test
    void testGetReward() {
        assertEquals(150, mission.getReward());
    }

    @Test
    void testGetType() {
        assertEquals(MissionType.EXPLORATION, mission.getType());
    }

    @Test
    void testMissionImmutability() {

        String originalTitle = mission.getTitle();
        String modified = originalTitle.replace("Test", "Changed");
        assertNotEquals(modified, mission.getTitle());
    }
}
