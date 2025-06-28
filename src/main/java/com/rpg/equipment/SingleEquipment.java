package com.rpg.equipment;

public class SingleEquipment extends EquipmentComponent {
    private Equipment equipment;

    public SingleEquipment(Equipment equipment) {
        super(equipment.getName());
        this.equipment = equipment;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    @Override
    public int getTotalAttackBonus() {
        return equipment.getAttackBonus();
    }

    @Override
    public int getTotalDefenseBonus() {
        return equipment.getDefenseBonus();
    }

    @Override
    public int getTotalValue() {
        return equipment.getValue();
    }

    @Override
    public void display(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        System.out.println(indent + equipment.toString());
    }
}
