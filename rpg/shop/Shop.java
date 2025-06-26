package rpg.shop;

import rpg.character.Character;
import rpg.equipment.Equipment;
import rpg.equipment.items.Weapon;
import rpg.equipment.items.Armor;
import rpg.equipment.EquipmentSlot;
import rpg.logging.GameLogger;
import rpg.validation.InputValidator;

import java.util.*;

public class Shop {
    private static final GameLogger logger = GameLogger.getInstance();
    private final Map<String, Equipment> inventory;
    private final List<Equipment> availableItems;

    public Shop() {
        this.inventory = new HashMap<>();
        this.availableItems = new ArrayList<>();
        initializeInventory();
    }

    private void initializeInventory() {
        // Weapons
        addItem(new Weapon("Iron Sword", 50, 8));
        addItem(new Weapon("Steel Sword", 100, 12));
        addItem(new Weapon("Enchanted Blade", 250, 20));
        addItem(new Weapon("Wooden Staff", 30, 6));
        addItem(new Weapon("Magic Wand", 80, 15));
        addItem(new Weapon("Fire Staff", 200, 25));
        addItem(new Weapon("Dagger", 25, 5));
        addItem(new Weapon("Poisoned Blade", 150, 18));

        // Armor pieces
        addItem(new Armor("Leather Armor", 40, 5, EquipmentSlot.ARMOR));
        addItem(new Armor("Chain Mail", 80, 8, EquipmentSlot.ARMOR));
        addItem(new Armor("Plate Armor", 160, 15, EquipmentSlot.ARMOR));
        addItem(new Armor("Magic Robes", 60, 6, EquipmentSlot.ARMOR));

        // Helmets
        addItem(new Armor("Iron Helmet", 30, 3, EquipmentSlot.HELMET));
        addItem(new Armor("Steel Helmet", 60, 5, EquipmentSlot.HELMET));
        addItem(new Armor("Wizard Hat", 45, 4, EquipmentSlot.HELMET));

        // Shields
        addItem(new Armor("Wooden Shield", 20, 4, EquipmentSlot.SHIELD));
        addItem(new Armor("Iron Shield", 50, 7, EquipmentSlot.SHIELD));
        addItem(new Armor("Tower Shield", 100, 12, EquipmentSlot.SHIELD));

        // Boots
        addItem(new Armor("Leather Boots", 15, 2, EquipmentSlot.BOOTS));
        addItem(new Armor("Steel Boots", 35, 4, EquipmentSlot.BOOTS));
        addItem(new Armor("Swift Boots", 80, 3, EquipmentSlot.BOOTS));

        logger.info("Shop inventory initialized with " + inventory.size() + " items");
    }

    private void addItem(Equipment item) {
        inventory.put(item.getName(), item);
        availableItems.add(item);
    }

    public void interact(Character character, Scanner scanner) {
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

        for (Equipment item : availableItems) {
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
        for (Equipment item : availableItems) {
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
        if (choice == 0) {
            return;
        }

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
        if (choice == 0) {
            return;
        }

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

    public <T extends Equipment> List<T> filterItems(Class<T> type, List<Equipment> items) {
        List<T> filtered = new ArrayList<>();
        for (Equipment item : items) {
            if (type.isInstance(item)) {
                filtered.add(type.cast(item));
            }
        }
        return filtered;
    }

    public List<Equipment> getSortedItemsByValue() {
        List<Equipment> sorted = new ArrayList<>(availableItems);
        Collections.sort(sorted, Comparator.comparing(Equipment::getValue));
        return sorted;
    }

    public List<Equipment> getSortedItemsByName() {
        List<Equipment> sorted = new ArrayList<>(availableItems);
        Collections.sort(sorted, Comparator.comparing(Equipment::getName));
        return sorted;
    }
}