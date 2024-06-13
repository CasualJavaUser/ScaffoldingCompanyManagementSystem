package org.example.entity;

import jakarta.persistence.*;
import org.example.Database;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "warehouse")
public class Warehouse extends Worksite {
    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    public Warehouse() {}

    public Warehouse(String address) {
        super(address);
        openingTime = LocalTime.of(7, 0, 0);
        closingTime = LocalTime.of(15, 0, 0);
    }

    public void setOpeningTime(LocalTime openingTime) {
        if (openingTime.until(closingTime, ChronoUnit.HOURS) < 8) {
            System.err.println("opening time must be at least 8 hours before closing time (" + closingTime + ")");
            return;
        }
        this.openingTime = LocalTime.of(openingTime.getHour(), openingTime.getMinute(), openingTime.getSecond());
    }

    public void setClosingTime(LocalTime closingTime) {
        if (openingTime.until(closingTime, ChronoUnit.HOURS) < 8) {
            System.err.println("closing time must be at least 8 hours after opening time (" + openingTime + ")");
            return;
        }
        this.closingTime = LocalTime.of(closingTime.getHour(), closingTime.getMinute(), closingTime.getSecond());
    }

    public static Set<Warehouse> getWarehouses() {
        return Database.selectFrom(Warehouse.class).stream().collect(Collectors.toSet());
    }

    public static Set<Warehouse> getActiveWarehouses() {
        return Database.selectFrom(Warehouse.class).stream()
                .filter(Worksite::isActive)
                .collect(Collectors.toSet());
    }
}
