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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "assembly_start_date", nullable = false)
    private LocalDate assemblyStartDate;

    @Column(name = "assembly_end_date", nullable = false)
    private LocalDate assemblyEndDate;

    @Column(name = "disassembly_start_date", nullable = false)
    private LocalDate disassemblyStartDate;

    @Column(name = "disassembly_end_date", nullable = false)
    private LocalDate disassemblyEndDate;

    @Column(name = "assembly_cost", nullable = false)
    private float assemblyCost;

    @Column(name = "disassembly_cost", nullable = false)
    private float disassemblyCost;

    @Column(name = "rental_cost", nullable = false)
    private float rentalCost;

    @Column(name = "total_cost", nullable = false)
    private float totalCost;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "comment")
    private String comment;

    public enum Status {
        CREATED("lightgray"),
        COMMENTED("lightyellow"),
        REVISED("lightyellow"),
        ACCEPTED("lightgreen"),
        REJECTED("pink"),
        ACTIVE("lightblue"),
        UNPAID("lightyellow"),
        CLOSED("rgb(128, 128, 128)");

        private String color;

        Status (String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public Offer() {}

    private Offer(String title, String description, LocalDate assemblyStartDate, LocalDate assemblyEndDate, LocalDate disassemblyStartDate, LocalDate disassemblyEndDate, float assemblyCost, float disassemblyCost, float rentalCost) {
        this.title = title;
        this.description = description;
        this.assemblyStartDate = assemblyStartDate;
        this.assemblyEndDate = assemblyEndDate;
        this.disassemblyStartDate = disassemblyStartDate;
        this.disassemblyEndDate = disassemblyEndDate;
        this.assemblyCost = assemblyCost;
        this.disassemblyCost = disassemblyCost;
        this.rentalCost = rentalCost;
        this.totalCost = calculateTotalCost();
        this.status = Status.CREATED;
        this.invoiceNumber = null;
        this.comment = null;
    }

    private float calculateTotalCost() {
        return assemblyCost + disassemblyCost + (disassemblyStartDate.toEpochDay() - assemblyEndDate.toEpochDay()) * rentalCost;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void moveDisassemblyDate(LocalDate newDate) {
        if (!newDate.isAfter(assemblyEndDate)) {
            System.err.println("new disassembly start date must be later than the assembly end date");
            return;
        }
        disassemblyEndDate = disassemblyEndDate.plus(disassemblyStartDate.until(newDate));
        disassemblyStartDate = LocalDate.of(newDate.getYear(), newDate.getMonth(), newDate.getDayOfMonth());
    }

    public void accept() {
        status = Status.ACCEPTED;
    }

    public void revise() {
        comment = null;
        status = Status.REVISED;
    }

    public void reject() {
        status = Status.REJECTED;
    }

    public void addComment(String comment) {
        this.comment = comment;
        status = Status.COMMENTED;
    }

    public void setInvoice(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        status = Status.CLOSED;
    }

    public static Offer createOffer(String title, String description, LocalDate assemblyStartDate, LocalDate assemblyEndDate, LocalDate disassemblyStartDate, LocalDate disassemblyEndDate, float assemblyCost, float disassemblyCost, float rentalCost) {
        if (!assemblyStartDate.isBefore(assemblyEndDate) || !disassemblyStartDate.isBefore(disassemblyEndDate) || !disassemblyStartDate.isAfter(assemblyEndDate)) {
            System.err.println("incorrect dates");
            return null;
        }
        return new Offer(title, description, assemblyStartDate, assemblyEndDate, disassemblyStartDate, disassemblyEndDate, assemblyCost, disassemblyCost, rentalCost);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
