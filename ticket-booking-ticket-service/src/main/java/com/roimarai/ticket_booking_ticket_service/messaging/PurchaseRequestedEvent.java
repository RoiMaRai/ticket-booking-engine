package com.roimarai.ticket_booking_ticket_service.messaging;

import java.math.BigDecimal;

public record PurchaseRequestedEvent(
        Long reservationId,
        String recipient,
        String paymentToken,
        BigDecimal price
) {
}