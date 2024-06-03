package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "foreman")
public class Foreman extends Labourer {

    public Foreman() {}

    public Foreman(String surname, String name) {
        super(surname, name);
    }
}
