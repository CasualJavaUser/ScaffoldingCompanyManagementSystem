package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(
        name = "manager_at_warehouse",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"date", "warehouse_manager_id"}),
                @UniqueConstraint(columnNames = {"date", "warehouse_id"})
        }
)
public class WarehouseManagerAtWarehouse {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "warehouse_manager_id", nullable = false)
    private Labourer warehouseManager;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public WarehouseManagerAtWarehouse() {}

    private WarehouseManagerAtWarehouse(Labourer warehouseManager, Warehouse warehouse, LocalDate date) {
        this.warehouseManager = warehouseManager;
        this.warehouse = warehouse;
        this.date = date;
    }

    public static boolean assign(Labourer warehouseManager, Warehouse warehouse, float hoursWorked) {
        return assign(warehouseManager, warehouse, LocalDate.now(), hoursWorked);
    }

    public static boolean assign(Labourer warehouseManager, Warehouse warehouse, LocalDate date, float hoursWorked) {
        if (!warehouseManager.isWarehouseManager()) {
            System.err.println("labourer " + warehouseManager.getFullName() + " is not a warehouse manager");
            return false;
        }
        Integer assignedWorksiteId = LabourerOnWorksite.getAssignedWorksiteId(warehouseManager, date);
        Integer overseenWarehouseId = getOverseenWarehouseId(warehouseManager, date);

        if (assignedWorksiteId == null) {
            if (!LabourerOnWorksite.canLabourerBeAssigned(warehouseManager, warehouse, date, false))
                return false;
            Database.persist(new WarehouseManagerAtWarehouse(warehouseManager, warehouse, date));
            LabourerOnWorksite.assign(warehouseManager, warehouse, date, hoursWorked);
            return true;
        }
        else if (assignedWorksiteId.equals(warehouse.getId())) {
            if (overseenWarehouseId == null) {
                Database.persist(new WarehouseManagerAtWarehouse(warehouseManager, warehouse, date));
                return true;
            }
            else if (overseenWarehouseId.equals(warehouse.getId())) {
                System.out.println("warehouse manager already assigned to the warehouse");
                return false;
            }
            else {
                throw new IllegalStateException("warehouse manager assigned to a different warehouse");
            }
        }
        else {
            System.out.println("labourer assigned to a different warehouse");
            return false;
        }
    }

    public static Integer getOverseenWarehouseId(Labourer warehouseManager, LocalDate date) {
        return Database.selectFrom(WarehouseManagerAtWarehouse.class).stream()
                .filter(wmaw -> wmaw.warehouseManager.getId().equals(warehouseManager.getId()))
                .filter(wmaw -> wmaw.date.equals(date))
                .findFirst()
                .map(wmaw -> wmaw.warehouse.getId())
                .orElse(null);
    }

    public static boolean isWarehouseManagerPresent(Warehouse warehouse, LocalDate date) {
        return Database.selectFrom(WarehouseManagerAtWarehouse.class).stream()
                .filter(wmaw -> wmaw.getWarehouseId().equals(warehouse.getId()))
                .anyMatch(wmaw -> wmaw.getDate().equals(date));
    }

    public static boolean isWarehouseManagerAssigned(Labourer warehouseManager, LocalDate date) {
        return Database.selectFrom(WarehouseManagerAtWarehouse.class).stream()
                .filter(wmaw -> wmaw.warehouseManager.getId().equals(warehouseManager.getId()))
                .anyMatch(wmaw -> wmaw.date.equals(date));
    }

    public static boolean dissociate(Labourer warehouseManager, LocalDate date) {
        if (!warehouseManager.isWarehouseManager()) {
            System.err.println("labourer is not a warehouse manager");
            return false;
        }
        Optional<WarehouseManagerAtWarehouse> optionalMWAW = Database.selectFrom(WarehouseManagerAtWarehouse.class).stream()
                .filter(mwaw -> mwaw.warehouse.getId().equals(warehouseManager.getId()))
                .filter(mwaw -> mwaw.date.equals(date))
                .findFirst();
        if (optionalMWAW.isEmpty()) {
            System.out.println("warehouse manager is not assigned to any construction site");
            return false;
        }
        if (LabourerOnWorksite.getAssignedCount(optionalMWAW.get().warehouse, date) > 1) {
            System.err.println("dissociate the rest of labourer before dissociating the warehouse manager");
            return false;
        }
        Database.remove(optionalMWAW.get());
        LabourerOnWorksite.dissociate(warehouseManager, date);
        return true;
    }

    public Integer getWarehouseManagerId() {
        return warehouseManager.getId();
    }

    public Integer getWarehouseId() {
        return warehouse.getId();
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date.toString() + ": " + warehouseManager.getFullName() + " -> " + warehouse.getAddress() + " )";
    }
}
