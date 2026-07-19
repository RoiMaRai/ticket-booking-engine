package com.roimarai.ticket_booking_ticket_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long eventId;
    private int seatCount;
    private String status;

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public String getStatus() {
        return status;
    }

    ////////////////////////////////

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
