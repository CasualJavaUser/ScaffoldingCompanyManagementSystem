package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "worksite")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Worksite {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "address")
    private String address;

    public Worksite() {}

    public Worksite(String address) {
        this.address = address;
    }

    public LabourerOnWorksite addLabourer(Labourer labourer, LocalDate date) {
        return new LabourerOnWorksite(labourer, this, date);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Worksite (address: " + address + ")";
    }
}
