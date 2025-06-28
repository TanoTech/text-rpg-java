package com.rpg.equipment;

public abstract class Equipment {
    protected String name;
    protected int value;
    protected EquipmentSlot slot;
    protected int attackBonus;
    protected int defenseBonus;

    public Equipment(String name, int value, EquipmentSlot slot, int attackBonus, int defenseBonus) {
        this.name = name;
        this.value = value;
        this.slot = slot;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public abstract String getDescription();

    @Override
    public String toString() {
        return name + " (Attack: +" + attackBonus + ", Defense: +" + defenseBonus + ", Value: " + value + "g)";
    }
}