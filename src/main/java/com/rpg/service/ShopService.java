package com.rpg.service;

import com.rpg.character.Character;
import com.rpg.equipment.Equipment;
import com.rpg.exceptions.GameException;
import java.util.List;
import java.util.Scanner;

public interface ShopService {
    List<Equipment> generateRandomInventory(int characterLevel) throws GameException;

    void visitShop(Character character, Scanner scanner) throws GameException;

    Equipment generateRandomEquipment(int characterLevel) throws GameException;

    List<Equipment> getAvailableItems();
}