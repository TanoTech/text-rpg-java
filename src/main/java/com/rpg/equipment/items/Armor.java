package com.rpg.equipment.items;

import com.rpg.equipment.Equipment;
import com.rpg.equipment.EquipmentSlot;

public class Armor extends Equipment {
    public Armor(String name, int value, int defenseBonus, EquipmentSlot slot) {
        super(name, value, slot, 0, defenseBonus);
    }

    @Override
    public String getDescription() {
        return "A piece of " + name.toLowerCase() + " that increases defense by " + defenseBonus;
    }
}