package rpg.equipment.items;

import rpg.equipment.Equipment;
import rpg.equipment.EquipmentSlot;

public class Weapon extends Equipment {
    public Weapon(String name, int value, int attackBonus) {
        super(name, value, EquipmentSlot.WEAPON, attackBonus, 0);
    }

    @Override
    public String getDescription() {
        return "A " + name.toLowerCase() + " that increases attack by " + attackBonus;
    }
}