package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse_manager")
public class WarehouseManager extends Labourer {

    public WarehouseManager() {}

    public WarehouseManager(String surname, String name) {
        super(surname, name);
    }
}
