package com.rpg.equipment;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSet extends EquipmentComponent {
    private List<EquipmentComponent> components;
    private int setBonusAttack;
    private int setBonusDefense;

    public EquipmentSet(String name, int setBonusAttack, int setBonusDefense) {
        super(name);
        this.components = new ArrayList<>();
        this.setBonusAttack = setBonusAttack;
        this.setBonusDefense = setBonusDefense;
    }

    @Override
    public void add(EquipmentComponent component) {
        components.add(component);
    }

    @Override
    public void remove(EquipmentComponent component) {
        components.remove(component);
    }

    @Override
    public List<EquipmentComponent> getChildren() {
        return new ArrayList<>(components);
    }

    @Override
    public int getTotalAttackBonus() {
        int total = setBonusAttack;
        for (EquipmentComponent component : components) {
            total += component.getTotalAttackBonus();
        }
        return total;
    }

    @Override
    public int getTotalDefenseBonus() {
        int total = setBonusDefense;
        for (EquipmentComponent component : components) {
            total += component.getTotalDefenseBonus();
        }
        return total;
    }

    @Override
    public int getTotalValue() {
        int total = 0;
        for (EquipmentComponent component : components) {
            total += component.getTotalValue();
        }
        return total;
    }

    @Override
    public void display(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        System.out.println(
                indent + name + " Set (Set bonus: +" + setBonusAttack + " attack, +" + setBonusDefense + " defense)");

        for (EquipmentComponent component : components) {
            component.display(depth + 1);
        }
    }
}