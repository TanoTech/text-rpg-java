package com.rpg.equipment;

import java.util.ArrayList;
import java.util.List;

public abstract class EquipmentComponent {
    protected String name;

    public EquipmentComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int getTotalAttackBonus();

    public abstract int getTotalDefenseBonus();

    public abstract int getTotalValue();

    public abstract void display(int depth);

    public void add(EquipmentComponent component) {
        throw new UnsupportedOperationException("Cannot add to leaf component");
    }

    public void remove(EquipmentComponent component) {
        throw new UnsupportedOperationException("Cannot remove from leaf component");
    }

    public List<EquipmentComponent> getChildren() {
        return new ArrayList<>();
    }
}
