package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "offer")
public class Offer {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "assembly_start_date")
    private LocalDate assemblyStartDate;

    @Column(name = "assembly_end_date")
    private LocalDate assemblyEndDate;

    @Column(name = "disassembly_start_date")
    private LocalDate disassemblyStartDate;

    @Column(name = "disassembly_end_date")
    private LocalDate disassemblyEndDate;

    @Column(name = "assembly_cost")
    private float assemblyCost;

    @Column(name = "disassembly_cost")
    private float disassemblyCost;

    public Offer() {}
}
