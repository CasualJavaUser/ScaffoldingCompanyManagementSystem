package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "manager")
public class Manager extends Employee {

    public Manager() {}

    public Manager(String surname, String name) {
        super(surname, name);
    }
}
