package com.rpg.service.impl;

import com.rpg.character.Character;
import com.rpg.equipment.Equipment;
import com.rpg.equipment.EquipmentSlot;
import com.rpg.equipment.items.Armor;
import com.rpg.equipment.items.Weapon;
import com.rpg.exceptions.GameException;
import com.rpg.logging.GameLogger;
import com.rpg.service.ShopService;
import com.rpg.validation.InputValidator;

import java.util.*;

public class ShopServiceImpl implements ShopService {
    private static final GameLogger logger = GameLogger.getInstance();
    private static final Random random = new Random();

    private static final String[] WEAPON_NAMES = {
            "Sword", "Blade", "Staff", "Wand", "Dagger", "Axe", "Mace", "Spear"
    };

    private static final String[] ARMOR_NAMES = {
            "Armor", "Mail", "Plate", "Robes", "Vest", "Tunic"
    };

    private static final String[] HELMET_NAMES = {
            "Helmet", "Hat", "Crown", "Cap", "Hood", "Circlet"
    };

    private static final String[] SHIELD_NAMES = {
            "Shield", "Buckler", "Guard", "Barrier"
    };

    private static final String[] BOOTS_NAMES = {
            "Boots", "Shoes", "Sandals", "Greaves"
    };

    private static final String[] QUALITY_PREFIXES = {
            "Rusty", "Iron", "Steel", "Silver", "Golden", "Enchanted", "Magical", "Legendary", "Epic", "Divine"
    };

    private final List<Equipment> currentInventory = new ArrayList<>();

    @Override
    public List<Equipment> generateRandomInventory(int characterLevel) throws GameException {
        currentInventory.clear();

        int itemCount = 8 + random.nextInt(8);

        for (int i = 0; i < itemCount; i++) {
            Equipment item = generateRandomEquipment(characterLevel);
            currentInventory.add(item);
        }

        logger.info("Generated " + itemCount + " items for level " + characterLevel + " character");
        return new ArrayList<>(currentInventory);
    }

    @Override
    public Equipment generateRandomEquipment(int characterLevel) throws GameException {
        EquipmentSlot slot = getRandomSlot();
        String name = generateRandomName(slot, characterLevel);
        int value = calculateValue(characterLevel);

        if (slot == EquipmentSlot.WEAPON) {
            int attack = calculateAttackBonus(characterLevel);
            return new Weapon(name, value, attack);
        } else {
            int defense = calculateDefenseBonus(characterLevel);
            return new Armor(name, value, defense, slot);
        }
    }

    private EquipmentSlot getRandomSlot() {
        EquipmentSlot[] slots = EquipmentSlot.values();
        return slots[random.nextInt(slots.length)];
    }

    private String generateRandomName(EquipmentSlot slot, int characterLevel) {
        String[] baseNames;

        switch (slot) {
            case WEAPON:
                baseNames = WEAPON_NAMES;
                break;
            case ARMOR:
                baseNames = ARMOR_NAMES;
                break;
            case HELMET:
                baseNames = HELMET_NAMES;
                break;
            case SHIELD:
                baseNames = SHIELD_NAMES;
                break;
            case BOOTS:
                baseNames = BOOTS_NAMES;
                break;
            default:
                baseNames = ARMOR_NAMES;
        }

        String baseName = baseNames[random.nextInt(baseNames.length)];
        String prefix = getQualityPrefix(characterLevel);

        return prefix + " " + baseName;
    }

    private String getQualityPrefix(int characterLevel) {
        int maxIndex = Math.min(QUALITY_PREFIXES.length - 1,
                (characterLevel / 2) + random.nextInt(3));
        maxIndex = Math.max(0, maxIndex);

        int minIndex = Math.max(0, maxIndex - 3);
        int index = minIndex + random.nextInt(maxIndex - minIndex + 1);

        return QUALITY_PREFIXES[index];
    }

    private int calculateValue(int characterLevel) {
        int baseValue = 20 + (characterLevel * 15);
        int variance = baseValue / 3;
        return baseValue + random.nextInt(variance * 2) - variance;
    }

    private int calculateAttackBonus(int characterLevel) {
        int baseAttack = 3 + (characterLevel / 2);
        int variance = Math.max(1, baseAttack / 3);
        return baseAttack + random.nextInt(variance * 2) - variance;
    }

    private int calculateDefenseBonus(int characterLevel) {
        int baseDefense = 2 + (characterLevel / 3);
        int variance = Math.max(1, baseDefense / 3);
        return baseDefense + random.nextInt(variance * 2) - variance;
    }

    @Override
    public void visitShop(Character character, Scanner scanner) throws GameException {
        generateRandomInventory(character.getLevel());

        boolean shopping = true;

        while (shopping) {
            displayShopMenu();
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayInventory(character);
                    break;
                case "2":
                    buyItem(character, scanner);
                    break;
                case "3":
                    sellItem(character, scanner);
                    break;
                case "4":
                    displayCharacterGold(character);
                    break;
                case "5":
                    shopping = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void displayShopMenu() {
        System.out.println("\n=== Welcome to the Shop ===");
        System.out.println("1. Browse Items");
        System.out.println("2. Buy Item");
        System.out.println("3. Sell Item");
        System.out.println("4. Check Gold");
        System.out.println("5. Leave Shop");
    }

    private void displayInventory(Character character) {
        System.out.println("\n=== Shop Inventory ===");
        System.out.println("Your gold: " + character.getGold());
        System.out.println();

        Map<EquipmentSlot, List<Equipment>> groupedItems = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            groupedItems.put(slot, new ArrayList<>());
        }

        for (Equipment item : currentInventory) {
            groupedItems.get(item.getSlot()).add(item);
        }

        for (Map.Entry<EquipmentSlot, List<Equipment>> entry : groupedItems.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            List<Equipment> items = entry.getValue();
            if (items.isEmpty())
                continue;

            System.out.println("--- " + slot + " ---");
            for (Equipment item : items) {
                String affordability = character.getGold() >= item.getValue()
                        ? "[Affordable]"
                        : "[Too Expensive]";
                System.out.println("  " + item + " " + affordability);
            }
            System.out.println();
        }
    }

    private void buyItem(Character character, Scanner scanner) {
        System.out.println("\n=== Buy Item ===");
        System.out.println("Your gold: " + character.getGold());

        List<Equipment> buyableItems = new ArrayList<>();
        for (Equipment item : currentInventory) {
            if (character.getGold() >= item.getValue()) {
                buyableItems.add(item);
            }
        }

        if (buyableItems.isEmpty()) {
            System.out.println("You cannot afford any items in the shop.");
            return;
        }

        System.out.println("Items you can afford:");
        for (int i = 0; i < buyableItems.size(); i++) {
            System.out.println((i + 1) + ". " + buyableItems.get(i));
        }

        System.out.print("Enter item number (0 to cancel): ");
        String input = scanner.nextLine().trim();

        if (!InputValidator.isValid(input, "^[0-9]+$")) {
            System.out.println("Invalid input.");
            return;
        }

        int choice = Integer.parseInt(input);
        if (choice == 0)
            return;

        if (choice < 1 || choice > buyableItems.size()) {
            System.out.println("Invalid item number.");
            return;
        }

        Equipment selectedItem = buyableItems.get(choice - 1);

        if (character.spendGold(selectedItem.getValue())) {
            Equipment oldItem = character.getEquipment().get(selectedItem.getSlot());
            if (oldItem != null) {
                System.out.println("Replaced " + oldItem.getName() + " with " + selectedItem.getName());
            } else {
                System.out.println("Equipped " + selectedItem.getName());
            }

            character.equipItem(selectedItem);
            currentInventory.remove(selectedItem); // Remove from shop inventory
            logger.info("Character " + character.getName() + " bought " + selectedItem.getName());
        } else {
            System.out.println("Not enough gold!");
        }
    }

    private void sellItem(Character character, Scanner scanner) {
        System.out.println("\n=== Sell Item ===");

        Map<EquipmentSlot, Equipment> equipment = character.getEquipment();
        if (equipment.isEmpty()) {
            System.out.println("You have no equipment to sell.");
            return;
        }

        System.out.println("Your equipment:");
        List<Map.Entry<EquipmentSlot, Equipment>> equipmentList = new ArrayList<>(equipment.entrySet());

        for (int i = 0; i < equipmentList.size(); i++) {
            Map.Entry<EquipmentSlot, Equipment> entry = equipmentList.get(i);
            int sellPrice = entry.getValue().getValue() / 2;
            System.out.println((i + 1) + ". " + entry.getValue() + " (Sell for: " + sellPrice + "g)");
        }

        System.out.print("Enter item number to sell (0 to cancel): ");
        String input = scanner.nextLine().trim();

        if (!InputValidator.isValid(input, "^[0-9]+$")) {
            System.out.println("Invalid input.");
            return;
        }

        int choice = Integer.parseInt(input);
        if (choice == 0)
            return;

        if (choice < 1 || choice > equipmentList.size()) {
            System.out.println("Invalid item number.");
            return;
        }

        Map.Entry<EquipmentSlot, Equipment> selectedEntry = equipmentList.get(choice - 1);
        Equipment itemToSell = selectedEntry.getValue();
        EquipmentSlot slot = selectedEntry.getKey();

        int sellPrice = itemToSell.getValue() / 2;
        character.addGold(sellPrice);
        character.unequipItem(slot);

        System.out.println("Sold " + itemToSell.getName() + " for " + sellPrice + " gold.");
        logger.info("Character " + character.getName() + " sold " + itemToSell.getName());
    }

    private void displayCharacterGold(Character character) {
        System.out.println("\nYou currently have " + character.getGold() + " gold.");
    }

    @Override
    public List<Equipment> getAvailableItems() {
        return new ArrayList<>(currentInventory);
    }
}