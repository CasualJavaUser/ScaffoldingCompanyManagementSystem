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

    public ConstructionSite() {}

    public ConstructionSite(String address) {
        super(address);
    }

    public static Set<ConstructionSite> getConstructionSites() {
        return Database.selectFrom(ConstructionSite.class).stream().collect(Collectors.toSet());
    }

    public static Set<ConstructionSite> getActiveConstructionSites() {
        return Database.selectFrom(ConstructionSite.class).stream()
                .filter(Worksite::isActive)
                .collect(Collectors.toSet());
    }
}
