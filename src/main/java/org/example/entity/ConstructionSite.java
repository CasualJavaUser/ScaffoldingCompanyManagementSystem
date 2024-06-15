package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "construction_site")
public class ConstructionSite extends Worksite {
    @Column(name = "is_at_heights", nullable = false)
    boolean isAtHeights;

    @OneToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    public ConstructionSite() {}

    public ConstructionSite(String address, boolean isAtHeights, Offer offer) {
        super(address);
        this.isAtHeights = isAtHeights;
        this.offer = offer;
    }

    public static Set<ConstructionSite> getConstructionSites() {
        return Database.selectFrom(ConstructionSite.class).stream().collect(Collectors.toSet());
    }

    public static Set<ConstructionSite> getActiveConstructionSites() {
        return Database.selectFrom(ConstructionSite.class).stream()
                .filter(Worksite::isActive)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isActive() {
        return !offer.getAssemblyStartDateCopy().isAfter(LocalDate.now()) && !offer.getDisassemblyEndDateCopy().isBefore(LocalDate.now());
    }

    @Override
    public void setInactive() {
        if (isActive())
            System.out.println("construction site cannot be set to inactive");
        else
            super.setInactive();
    }

    @Override
    public String toString() {
        return "ConstructionSite{" +
                "address=" + getAddress() +
                ", isAtHeights=" + isAtHeights +
                ", offerId=" + offer.getId() +
                '}';
    }
}
