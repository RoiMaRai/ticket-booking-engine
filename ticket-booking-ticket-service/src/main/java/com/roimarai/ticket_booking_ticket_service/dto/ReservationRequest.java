package com.roimarai.ticket_booking_ticket_service.model;

public record ReservationRequest(
        int seatCount,
        String recipient,
        String paymentToken
) {
}
