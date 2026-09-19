package com.roimarai.ticket_booking_ticket_service.dto;

import com.roimarai.ticket_booking_ticket_service.model.Reservation;

import java.math.BigDecimal;

public record ReservationResult(
        Reservation reservation,
        BigDecimal totalPrice
) {
}