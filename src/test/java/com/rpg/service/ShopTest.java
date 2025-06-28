package com.rpg.service;

import com.rpg.character.Character;
import com.rpg.equipment.Equipment;
import com.rpg.equipment.EquipmentSlot;
import com.rpg.equipment.items.Weapon;
import com.rpg.exceptions.GameException;
import com.rpg.service.impl.ShopServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ShopService Tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShopTest {

    @Mock
    private Character mockCharacter;
    private ShopServiceImpl shopService;
    private ByteArrayOutputStream out;
    private PrintStream orig;

    @BeforeEach
    void setUp() {
        shopService = new ShopServiceImpl();
        out = new ByteArrayOutputStream();
        orig = System.out;
        System.setOut(new PrintStream(out));
    }

    @Test
    @DisplayName("visitShop: invalid option")
    void visitShop_invalidOption() throws GameException {
        Scanner sc = new Scanner(new ByteArrayInputStream("99\n5\n".getBytes()));
        shopService.visitShop(mockCharacter, sc);
        assertTrue(out.toString().contains("Invalid option"));
    }

    @Test
    @DisplayName("sellItem: no equipment")
    void sellItem_noEquip() throws GameException {
        when(mockCharacter.getEquipment()).thenReturn(Map.of());
        Scanner sc = new Scanner(new ByteArrayInputStream("3\n5\n".getBytes()));
        shopService.visitShop(mockCharacter, sc);
        assertTrue(out.toString().contains("You have no equipment to sell"));
    }

    @Test
    @DisplayName("sellItem: success")
    void sellItem_success() throws GameException {
        var eq = new HashMap<EquipmentSlot, Equipment>();
        eq.put(EquipmentSlot.WEAPON, new Weapon("Sword", 100, 5));
        when(mockCharacter.getEquipment()).thenReturn(eq);
        Scanner sc = new Scanner(new ByteArrayInputStream("3\n1\n5\n".getBytes()));
        shopService.visitShop(mockCharacter, sc);
        verify(mockCharacter).addGold(50);
        verify(mockCharacter).unequipItem(EquipmentSlot.WEAPON);
        assertTrue(out.toString().contains("Sold"));
    }

    @Test
    @DisplayName("buyItem: success")
    void buyItem_success() throws GameException {
        when(mockCharacter.getLevel()).thenReturn(5);
        when(mockCharacter.getGold()).thenReturn(1000);
        when(mockCharacter.spendGold(anyInt())).thenReturn(true);
        when(mockCharacter.getEquipment()).thenReturn(Map.of());
        Scanner sc = new Scanner(new ByteArrayInputStream("2\n1\n5\n".getBytes()));
        shopService.visitShop(mockCharacter, sc);
        verify(mockCharacter).spendGold(anyInt());
        verify(mockCharacter).equipItem(any(Equipment.class));
    }

    @Test
    @DisplayName("buyItem: insufficient gold")
    void buyItem_insufficientGold() throws GameException {
        when(mockCharacter.getGold()).thenReturn(0);
        Scanner sc = new Scanner(new ByteArrayInputStream("2\n5\n".getBytes()));
        shopService.visitShop(mockCharacter, sc);
        assertTrue(out.toString().contains("You cannot afford any items"));
    }

    @Test
    @DisplayName("checkGold: displays gold")
    void checkGold() throws GameException {
        when(mockCharacter.getGold()).thenReturn(250);
        Scanner sc = new Scanner(new ByteArrayInputStream("4\n5\n".getBytes()));
        shopService.visitShop(mockCharacter, sc);
        assertTrue(out.toString().contains("You currently have 250 gold"));
    }

    @Test
    @DisplayName("getAvailableItems: defensive copy")
    void getAvailableItems_copy() throws GameException {
        shopService.generateRandomInventory(5);
        var list1 = shopService.getAvailableItems();
        list1.clear();
        assertFalse(shopService.getAvailableItems().isEmpty());
    }

    @Test
    void restoreOut() {
        System.setOut(orig);
    }
}
