package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.io.File;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "employee")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_currently_employed", nullable = false)
    private boolean isCurrentlyEmployed;

    public Employee() {}

    public Employee(String surname, String name) {
        this.surname = surname;
        this.name = name;
        this.isCurrentlyEmployed = true;
        this.phone = null;
    }

    public void fire() {
        isCurrentlyEmployed = false;
    }

    public String getFullName() {
        return surname + " " + name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (id: " + id + " full name: " + getFullName() + ")";
    }

    public static File getMonthlySummary(int year, int month) {
        return null; //TODO getMonthlySummary
    }

    public static File getMonthlySummary() {
        return null; //TODO getMonthlySummary
    }

    public static void generateDailySummary() {

    }

    public static File getDailySummary() {
        return null; //TODO getDailySummary
    }

    public static Set<Employee> getEmployees() {
        return Database.selectFrom(Employee.class).stream().collect(Collectors.toSet());
    }

    public static Set<Employee> getCurrentlyEmployed() {
        return Database.selectFrom(Employee.class).stream()
                .filter(Employee::isCurrentlyEmployed)
                .collect(Collectors.toSet());
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public boolean isCurrentlyEmployed() {
        return isCurrentlyEmployed;
    }
}
