package com.roimarai.ticket_booking_payment_service.messaging;

import com.roimarai.ticket_booking_shared.messaging.PurchaseRequestedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {
    @KafkaListener(topics = "ticket-purchase-requested", groupId = "payment-group")
    public void consumePurchaseRequested(PurchaseRequestedEvent event){
        System.out.println("==================================");
        System.out.println("RECEIVED");
        System.out.println("Event: " + event);
        System.out.println("Reservation ID: " + event.reservationId());
        System.out.println("Price: " + event.price());
        System.out.println("Reserved for: " + event.recipient());
        System.out.println("Charge from: " + event.paymentToken());
        System.out.println("==================================");
    }
}