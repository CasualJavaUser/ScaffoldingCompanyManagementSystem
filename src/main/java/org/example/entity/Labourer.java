package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "labourer")
@Inheritance(strategy = InheritanceType.JOINED)
public class Labourer extends Employee {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "labourer_id")
    private final Map<Qualification.Type, Qualification> qualifications = new HashMap<>();

    public Labourer() {}

    public Labourer(String surname, String name) {
        super(surname, name);
    }

    public void addQualification(Qualification.Type type, LocalDate expiryDate) {
        qualifications.put(type, new Qualification(type, expiryDate));
    }

    @Override
    public String toString() {
        return super.toString() + "\nqualifications: " + qualifications.values().stream().map(Qualification::toString).collect(Collectors.joining("\n - "));
    }
}