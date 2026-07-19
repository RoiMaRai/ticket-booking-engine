package com.roimarai.ticket_booking_ticket_service.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private BigDecimal price;
    private String venueName;
    private int totalSeats;
    private int availableSeats;

    public Long getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getVenueName() {
        return venueName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    //////////////////////////////////////////

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public void setTotalSeats(int capacity) {
        this.totalSeats = capacity;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    @PrePersist
    public void initAvailableSeats() {
        this.availableSeats = this.totalSeats;
    }
}
