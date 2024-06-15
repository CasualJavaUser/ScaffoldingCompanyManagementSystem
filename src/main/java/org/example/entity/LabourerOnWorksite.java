package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(
        name = "labourer_on_worksite",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"date", "labourer_id"})}
)
public class LabourerOnWorksite {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "labourer_id", nullable = false)
    private Labourer labourer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workste_id", nullable = false)
    private Worksite worksite;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "hours_worked", nullable = false)
    private float hoursWorked;

    public LabourerOnWorksite() {}

    private LabourerOnWorksite(Labourer labourer, Worksite worksite, LocalDate date, float hoursWorked) {
        this.labourer = labourer;
        this.worksite = worksite;
        this.date = date;
        this.hoursWorked = hoursWorked;
    }

    /**
     * Assigns to a labourer to a worksite on the current date.
     * Checks if the labourer has valid required qualifications and is already assigned to a different worksite.
     * @param labourer the labourer to be assigned
     * @param worksite the worksite the labourer is to be assigned to
     * @param hoursWorked the number of hours that the labourer worked for
     * @return true if the association was successfully created
     */
    public static boolean assign(Labourer labourer, Worksite worksite, float hoursWorked) {
        return assign(labourer, worksite, LocalDate.now(), hoursWorked);
    }

    /**
     * Assigns to a labourer to a worksite on a specified date.
     * Checks if the labourer has valid required qualifications and is already assigned to a different worksite.
     * @param labourer the labourer to be assigned
     * @param worksite the worksite the labourer is to be assigned to
     * @param date the date on which the labourer worked on the worksite
     * @param hoursWorked the number of hours that the labourer worked for
     * @return true if the association was successfully created
     */
    public static boolean assign(Labourer labourer, Worksite worksite, LocalDate date, float hoursWorked) {
        if (!canLabourerBeAssigned(labourer, worksite, date, true))
            return false;
        Database.persist(new LabourerOnWorksite(labourer, worksite, date, hoursWorked));
        return true;
    }

    public static boolean dissociate(Labourer labourer, LocalDate date) {
        Optional<LabourerOnWorksite> low = Database.selectFrom(LabourerOnWorksite.class).stream()
                .filter(l -> l.labourer.getId().equals(labourer.getId()))
                .filter(l -> l.date.equals(date))
                .findFirst();

        if (low.isEmpty()) {
            System.out.println("labourer is not assigned to any worksite");
            return false;
        }
        if (labourer.isForeman() && ForemanAtConstruction.isForemanAssigned(labourer, date)) {
            System.err.println("labourer is assigned as a foreman at a construction site");
            return false;
        }
        if (labourer.isWarehouseManager() && WarehouseManagerAtWarehouse.isWarehouseManagerAssigned(labourer, date)) {
            System.err.println("labourer is assigned as a warehouse manager at a warehouse");
            return false;
        }
        Database.remove(low.get());
        return true;
    }

    public static boolean canLabourerBeAssigned(Labourer labourer, Worksite worksite, LocalDate date, boolean checkForSupervisor) {
        //OHS training
        if (!labourer.hasValidQualification(Qualification.Type.OHS_TRAINING)) {
            System.err.println("labourer " + labourer.getFullName() + " does not have a valid OHS training");
            return false;
        }
        //scaffolder
        if (worksite instanceof ConstructionSite cs && cs.isAtHeights && !labourer.hasValidQualification(Qualification.Type.SCAFFOLDER)) {
            System.err.println("labourer has to have valid scaffolder qualifications to work at this worksite (" + worksite.getAddress() + ")");
            return false;
        }
        //is assigned to a different worksite
        if (isAssigned(labourer, date)) {
            System.err.println("labourer is already assigned to a worksite.");
            return false;
        }
        //is foreman or warehouse manager assigned to worksite
        if (checkForSupervisor) {
            if (worksite instanceof ConstructionSite cs && !ForemanAtConstruction.isForemanPresent(cs, date)) {
                System.err.println("labourer cannot be assigned to a construction site at " + cs.getAddress() + " because a foreman has not been assigned yet");
                return false;
            }
            if (worksite instanceof Warehouse w && !WarehouseManagerAtWarehouse.isWarehouseManagerPresent(w, date)) {
                System.err.println("labourer cannot be assigned to a construction site at " + w.getAddress() + " because a foreman has not been assigned yet");
                return false;
            }
        }
        return true;
    }

    public static Integer getAssignedWorksiteId(Labourer labourer, LocalDate date) {
        return Database.selectFrom(LabourerOnWorksite.class).stream()
                .filter(low -> low.labourer.getId().equals(labourer.getId()))
                .filter(low -> low.date.equals(date))
                .findFirst()
                .map(low -> low.worksite.getId())
                .orElse(null);
    }

    public static boolean isAssigned(Labourer labourer, LocalDate date) {
        return Database.selectFrom(LabourerOnWorksite.class).stream()
                .filter(low -> low.labourer.getId().equals(labourer.getId()))
                .anyMatch(low -> low.date.equals(date));
    }

    public static boolean isAnyAssigned(Worksite worksite, LocalDate date) {
        return Database.selectFrom(LabourerOnWorksite.class).stream()
                .filter(low -> low.worksite.getId().equals(worksite.getId()))
                .anyMatch(low -> low.date.equals(date));
    }

    public static long getAssignedCount(Worksite worksite, LocalDate date) {
        return Database.selectFrom(LabourerOnWorksite.class).stream()
                .filter(low -> low.worksite.getId().equals(worksite.getId()))
                .filter(low -> low.date.equals(date))
                .count();
    }

    /**
     * Sets the number of hours the labourer worked for
     */
    public static void setHoursWorked(Labourer labourer, LocalDate date, float hoursWorked) {
        if (hoursWorked > 8 + Labourer.maxOvertime) {
            System.err.println("labourers cannot work more than " + (8 + Labourer.maxOvertime) + " hours a day");
            return;
        }
        Optional<LabourerOnWorksite> optionalLOW = Database.selectFrom(LabourerOnWorksite.class).stream()
                .filter(low -> low.labourer.getId().equals(labourer.getId()))
                .filter(low -> low.date.equals(date))
                .findFirst();
        if (optionalLOW.isEmpty()) {
            System.err.println("labourer " + labourer.getFullName() + " has not been assigned to any worksite on " + date);
            return;
        }
        optionalLOW.get().hoursWorked = hoursWorked;
        Database.merge(optionalLOW.get());
    }

    @Override
    public String toString() {
        return date.toString() + ": " + labourer.getFullName() + " -> " + worksite.getAddress();
    }
}
