package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "labourer")
@Inheritance(strategy = InheritanceType.JOINED)
public class Labourer extends Employee {
    public static float baseWage = 28.1f;
    public static float maxOvertime = 2;

    @Column(name = "wage", nullable = false)
    private float wage;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "labourer_id", nullable = false)
    private final Map<Qualification.Type, Qualification> qualifications = new HashMap<>();

    @Column(name = "is_foreman", nullable = false)
    private boolean isForeman;

    @Column(name = "is_warehouse_manager", nullable = false)
    private boolean isWarehouseManager;

    public Labourer() {}

    public Labourer(String surname, String name) {
        super(surname, name);
        wage = baseWage;
        isForeman = false;
        isWarehouseManager = false;
    }

    public void addQualification(Qualification.Type type, LocalDate expiryDate) {
        qualifications.put(type, new Qualification(type, expiryDate));
    }

    public void makeForeman(boolean makeForeman) {
        isForeman = makeForeman;
    }

    public void makeWarehouseManager(boolean makeWManager) {
        isWarehouseManager = makeWManager;
    }

    public boolean isForeman() {
        return isForeman;
    }

    public boolean isWarehouseManager() {
        return isWarehouseManager;
    }

    public boolean hasValidQualification(Qualification.Type qualification) {
        return qualifications.get(qualification) != null &&
                !qualifications.get(qualification).expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public void fire() {
        if (LabourerOnWorksite.isAssigned(this, LocalDate.now()))
            System.err.println("labourer is assigned to a worksite and cannot be fired");
        else
            super.fire();
    }

    @Override
    public String toString() {
        return "\nLabourer (id: " + getId() + " full name: " + getFullName() + " is foreman: " + isForeman + " is warehouse manager: " + isWarehouseManager + ")"
                + "\nqualifications: " + qualifications.values().stream().map(Qualification::toString).collect(Collectors.joining("\n - "));
    }
}