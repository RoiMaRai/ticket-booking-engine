package com.roimarai.ticket_booking_ticket_service.model;

import java.math.BigDecimal;

public record ReservationResult(
        Reservation reservation,
        BigDecimal totalPrice
) {
}