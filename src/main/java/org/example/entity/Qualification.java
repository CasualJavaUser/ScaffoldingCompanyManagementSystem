package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "qualification")
public class Qualification {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "expiry_date", nullable = false)
    LocalDate expiryDate;

    public enum Type {
        FORKLIFT,
        BACKHOE_LOADER,
        SCAFFOLDER,
        DRIVERS_LICENSE_B,
        DRIVERS_LICENSE_D,
        OHS_TRAINING
    }

    public Qualification () {}

    public Qualification(Type type, LocalDate expiryDate) {
        this.type = type;
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return type + " expires: " + expiryDate.toString();
    }
}
