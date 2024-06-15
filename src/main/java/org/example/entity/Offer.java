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

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "comment")
    private String comment;

    public enum Status {
        ALL(null), //only for UI purposes
        CREATED("lightgray"),
        COMMENTED("lightyellow"),
        REVISED("lightyellow"),
        ACCEPTED("lightgreen"),
        REJECTED("pink"),
        ACTIVE("lightblue"),
        UNPAID("lightyellow"),
        CLOSED("rgb(128, 128, 128)");

        private final String color;

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
        this.status = Status.CREATED;
        this.invoiceNumber = null;
        this.comment = null;
    }

    public float getTotalCost() {
        return calculateTotalCost(assemblyEndDate, disassemblyStartDate, assemblyCost, disassemblyCost, rentalCost);
    }

    public static float calculateTotalCost(LocalDate assemblyEndDate, LocalDate disassemblyStartDate, float assemblyCost, float disassemblyCost, float rentalCost) {
        return assemblyCost + disassemblyCost + (disassemblyStartDate.toEpochDay() - assemblyEndDate.toEpochDay()) * rentalCost;
    }

    public void moveDisassemblyDate(LocalDate newDate) {
        if (!isDisassemblyDateValid(newDate)) {
            System.err.println("new disassembly start date must be later than the assembly end date");
            return;
        }
        disassemblyEndDate = disassemblyEndDate.plus(disassemblyStartDate.until(newDate));
        disassemblyStartDate = LocalDate.of(newDate.getYear(), newDate.getMonth(), newDate.getDayOfMonth());
    }

    public boolean isDisassemblyDateValid(LocalDate newDate) {
        return newDate.isAfter(assemblyEndDate);
    }

    public void accept() {
        status = Status.ACCEPTED;
    }

    public void revise(String description, LocalDate assemblyStartDate, LocalDate assemblyEndDate, LocalDate disassemblyStartDate, LocalDate disassemblyEndDate, float assemblyCost, float disassemblyCost, float rentalCost) {
        this.description = description;
        this.assemblyStartDate = assemblyStartDate;
        this.assemblyEndDate = assemblyEndDate;
        this.disassemblyStartDate = disassemblyStartDate;
        this.disassemblyEndDate = disassemblyEndDate;
        this.assemblyCost = assemblyCost;
        this.disassemblyCost = disassemblyCost;
        this.rentalCost = rentalCost;
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

    public static Offer createOffer(String title, String description, LocalDate assemblyStartDate, LocalDate assemblyEndDate, LocalDate disassemblyStartDate, LocalDate disassemblyEndDate, float assemblyCost, float disassemblyCost, float rentalCost, Client client) {
        String message = isOfferValid(title, description, assemblyStartDate, assemblyEndDate, disassemblyStartDate, disassemblyEndDate, assemblyCost, disassemblyCost, rentalCost, client);
        if (message != null) {
            System.err.println(message);
            return null;
        }
        Offer offer = new Offer(title, description, assemblyStartDate, assemblyEndDate, disassemblyStartDate, disassemblyEndDate, assemblyCost, disassemblyCost, rentalCost);
        client.addOffer(offer);
        return offer;
    }

    public static String isOfferValid(String title, String description, LocalDate assemblyStartDate, LocalDate assemblyEndDate, LocalDate disassemblyStartDate, LocalDate disassemblyEndDate, float assemblyCost, float disassemblyCost, float rentalCost, Client client) {
        if (title == null || title.isEmpty())
            return "Offer title cannot be empty.";
        if (description == null || description.isEmpty())
            return "Offer description cannot be empty.";
        if (assemblyStartDate == null || assemblyEndDate == null || disassemblyStartDate == null || disassemblyEndDate == null)
            return "All dates are required.";
        if (!assemblyStartDate.isBefore(assemblyEndDate))
            return "The assembly start date must be before the assembly end date.";
        if (!disassemblyStartDate.isBefore(disassemblyEndDate))
            return "The disassembly start date must be before disassembly end date.";
        if (!disassemblyStartDate.isAfter(assemblyEndDate))
            return "The disassembly start date must be after the assembly end date.";
        if (assemblyCost < 0)
            return "The assembly cost cannot be less than 0.";
        if (disassemblyCost < 0)
            return "The disassembly cost cannot be less than 0.";
        if (rentalCost < 0)
            return "The rental cost cannot be less than 0.";
        if (client == null) {
            return "Client must not be null";
        }
        return null;
    }

    public Status getStatus() {
        if (status == Status.ACCEPTED && !assemblyStartDate.isAfter(LocalDate.now()))
            status = Status.ACTIVE;
        else if (status == Status.ACTIVE && disassemblyEndDate.isBefore(LocalDate.now()))
            status = Status.UNPAID;
        return status;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAssemblyStartDateString() {
        return assemblyStartDate.toString();
    }

    public String getAssemblyEndDateString() {
        return assemblyEndDate.toString();
    }

    public String getDisassemblyStartDateString() {
        return disassemblyStartDate.toString();
    }

    public String getDisassemblyEndDateString() {
        return disassemblyEndDate.toString();
    }

    public LocalDate getAssemblyStartDateCopy() {
        return LocalDate.from(assemblyStartDate);
    }

    public LocalDate getAssemblyEndDateCopy() {
        return LocalDate.from(assemblyEndDate);
    }

    public LocalDate getDisassemblyStartDateCopy() {
        return LocalDate.from(disassemblyStartDate);
    }

    public LocalDate getDisassemblyEndDateCopy() {
        return LocalDate.from(disassemblyEndDate);
    }

    public float getAssemblyCost() {
        return assemblyCost;
    }

    public float getDisassemblyCost() {
        return disassemblyCost;
    }

    public float getRentalCost() {
        return rentalCost;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", assemblyStartDate=" + assemblyStartDate +
                ", disassemblyStartDate=" + disassemblyStartDate +
                ", assemblyCost=" + assemblyCost +
                ", disassemblyCost=" + disassemblyCost +
                ", rentalCost=" + rentalCost +
                ", status=" + status +
                '}';
    }
}
