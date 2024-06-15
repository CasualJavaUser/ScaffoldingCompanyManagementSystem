package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;

@Entity
@Table(name = "worksite")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Worksite {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public Worksite() {}

    public Worksite(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInactive() {
        isActive = false;
    }

    public Integer getId() {
        return id;
    }
}
