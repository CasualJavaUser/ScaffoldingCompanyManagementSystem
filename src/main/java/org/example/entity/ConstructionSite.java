package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "construction_site")
public class ConstructionSite extends Worksite {
    @Column(name = "is_at_heights")
    boolean isAtHeights;

    @OneToMany
    @JoinColumn(name = "construction_site_id")
    private final Map<LocalDate, Foreman> foremen = new HashMap<>();

    public ConstructionSite() {}

    public ConstructionSite(String address) {
        super(address);
    }

    public void setForeman(Foreman foreman, LocalDate date) {
        foremen.put(date, foreman);
    }

    public void setForemanToday(Foreman foreman) {
        foremen.put(LocalDate.now(), foreman);
    }
}
