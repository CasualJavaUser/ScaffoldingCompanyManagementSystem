package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "warehouse")
public class Warehouse extends Worksite {
    @OneToMany
    @JoinColumn(name = "warehouse_id")
    private final Map<LocalDate, WarehouseManager> managers = new HashMap<>();

    public Warehouse() {}

    public Warehouse(String address) {
        super(address);
    }

    public void setManager(WarehouseManager manager, LocalDate date) {
        managers.put(date, manager);
    }

    public void setManagerToday(WarehouseManager manager) {
        managers.put(LocalDate.now(), manager);
    }
}
