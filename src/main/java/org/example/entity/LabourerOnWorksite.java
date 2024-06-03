package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "labourer_on_worksite")
public class LabourerOnWorksite {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "labourer_id")
    private Labourer labourer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workste")
    private Worksite worksite;

    @Column(name = "date")
    private LocalDate date;

    public LabourerOnWorksite() {}

    public LabourerOnWorksite(Labourer labourer, Worksite worksite, LocalDate date) {
        this.labourer = labourer;
        this.worksite = worksite;
        this.date = date;
    }

    @Override
    public String toString() {
        return date.toString() + ": " + labourer.getFullName() + " -> " + worksite.getAddress() + " )";
    }
}
