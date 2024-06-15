package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(
        name = "foreman_at_construction",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"date", "foreman_id"}),
                @UniqueConstraint(columnNames = {"date", "construction_id"})
        }
)
public class ForemanAtConstruction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "foreman_id", nullable = false)
    private Labourer foreman;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "construction_id", nullable = false)
    private ConstructionSite constructionSite;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public ForemanAtConstruction() {}

    private ForemanAtConstruction(Labourer foreman, ConstructionSite constructionSite, LocalDate date) {
        this.foreman = foreman;
        this.constructionSite = constructionSite;
        this.date = date;
    }

    public static boolean assign(Labourer labourer, ConstructionSite constructionSite, float hoursWorked) {
        return assign(labourer, constructionSite, LocalDate.now(), hoursWorked);
    }

    public static boolean assign(Labourer foreman, ConstructionSite constructionSite, LocalDate date, float hoursWorked) {
        if (!foreman.isForeman()) {
            System.err.println("labourer " + foreman.getFullName() + " is not a foreman");
            return false;
        }
        Integer assignedWorksiteId = LabourerOnWorksite.getAssignedWorksiteId(foreman, date);
        Integer overseenConstructionId = getOverseenConstructionId(foreman, date);

        if (assignedWorksiteId == null) {
            if (!LabourerOnWorksite.canLabourerBeAssigned(foreman, constructionSite, date, false))
                return false;
            Database.persist(new ForemanAtConstruction(foreman, constructionSite, date));
            LabourerOnWorksite.assign(foreman, constructionSite, date, hoursWorked);
            return true;
        }
        else if (assignedWorksiteId.equals(constructionSite.getId())) {
            if (overseenConstructionId == null) {
                Database.persist(new ForemanAtConstruction(foreman, constructionSite, date));
                return true;
            }
            else if (overseenConstructionId.equals(constructionSite.getId())) {
                System.out.println("foreman already assigned to the construction site");
                return false;
            }
            else {
                throw new IllegalStateException("foreman assigned to a different construction site");
            }
        }
        else {
            System.out.println("labourer assigned to a different construction site");
            return false;
        }
    }

    public static boolean dissociate(Labourer foreman, LocalDate date) {
        if (!foreman.isForeman()) {
            System.err.println("labourer is not a foreman");
            return false;
        }
        Optional<ForemanAtConstruction> optionalFAC = Database.selectFrom(ForemanAtConstruction.class).stream()
                .filter(fac -> fac.foreman.getId().equals(foreman.getId()))
                .filter(fac -> fac.date.equals(date))
                .findFirst();
        if (optionalFAC.isEmpty()) {
            System.out.println("foreman is not assigned to any construction site");
            return false;
        }
        if (LabourerOnWorksite.getAssignedCount(optionalFAC.get().constructionSite, date) > 1) {
            System.err.println("dissociate the rest of labourer before dissociating the foreman");
            return false;
        }
        Database.remove(optionalFAC.get());
        LabourerOnWorksite.dissociate(foreman, date);
        return true;
    }

    public static Integer getOverseenConstructionId(Labourer foreman, LocalDate date) {
        return Database.selectFrom(ForemanAtConstruction.class).stream()
                .filter(fac -> fac.foreman.getId().equals(foreman.getId()))
                .filter(fac -> fac.date.equals(date))
                .findFirst()
                .map(fac -> fac.constructionSite.getId())
                .orElse(null);
    }

    public static boolean isForemanPresent(ConstructionSite constructionSite, LocalDate date) {
        return Database.selectFrom(ForemanAtConstruction.class).stream()
                .filter(fac -> fac.getConstructionSiteId().equals(constructionSite.getId()))
                .anyMatch(fac -> fac.getDate().equals(date));
    }

    public Integer getForemanId() {
        return foreman.getId();
    }

    public Integer getConstructionSiteId() {
        return constructionSite.getId();
    }

    public static boolean isForemanAssigned(Labourer foreman, LocalDate date) {
        return Database.selectFrom(ForemanAtConstruction.class).stream()
                .filter(fac -> fac.foreman.getId().equals(foreman.getId()))
                .anyMatch(fac -> fac.date.equals(date));
    }

    @Override
    public String toString() {
        return date.toString() + ": " + foreman.getFullName() + " -> " + constructionSite.getAddress() + " )";
    }

    public LocalDate getDate() {
        return date;
    }
}
