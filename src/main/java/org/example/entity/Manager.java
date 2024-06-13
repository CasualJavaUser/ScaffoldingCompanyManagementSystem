package org.example.entity;

import jakarta.persistence.*;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "manager")
public class Manager extends Employee {
    public static float baseSalary = 5000f;

    @Column(name = "email")
    private String email;

    @Column(name = "salary", nullable = false)
    private float salary;

    @Column(name = "previous_experience")
    private String previousExperience;

    @OneToMany
    @JoinColumn(name = "manager_id")
    private final SortedSet<ConstructionSite> constructionSites =
            new TreeSet<>(Comparator.comparing(ConstructionSite::getAddress));

    public Manager() {}

    public Manager(String surname, String name) {
        super(surname, name);
        salary = baseSalary;
        email = null;
        previousExperience = null;
    }

    public void setSalary(float salary) {
        if (salary < baseSalary) {
            System.err.println("salary must not be lower than the base salary (" + baseSalary + ")");
            return;
        }
        this.salary = salary;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPreviousExperience(String previousExperience) {
        this.previousExperience = previousExperience;
    }
}
