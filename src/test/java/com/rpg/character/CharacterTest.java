package com.rpg.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterTest {

    private Character hero;

    @BeforeEach
    void setUp() {
        hero = new Character("Arthas", "Warrior", 100, 10);
    }

    @Test
    void testInitialStats() {
        assertEquals("Arthas", hero.getName());
        assertEquals("Warrior", hero.getCharacterClass());
        assertEquals(1, hero.getLevel());
        assertEquals(100, hero.getHealth());
        assertEquals(100, hero.getMaxHealth());
        assertEquals(10, hero.getAttack());
        assertEquals(100, hero.getGold());
        assertEquals(0, hero.getExperience());
        assertTrue(hero.getEquipment().isEmpty());
    }

    @Test
    void testHealthBounds() {
        hero.setHealth(150);
        assertEquals(100, hero.getHealth());
        hero.setHealth(-20);
        assertEquals(0, hero.getHealth());
    }

    @Test
    void testGoldOperations() {
        hero.addGold(50);
        assertEquals(150, hero.getGold());
        assertTrue(hero.spendGold(100));
        assertEquals(50, hero.getGold());
        assertFalse(hero.spendGold(100));
        assertEquals(50, hero.getGold());
    }

    @Test
    void testExperienceAndLevelUp() {
        hero.addExperience(120);
        assertEquals(2, hero.getLevel());
        assertEquals(20, hero.getExperience());
        assertEquals(110, hero.getMaxHealth());
        assertEquals(110, hero.getHealth());
        assertEquals(12, hero.getAttack());
    }
}
