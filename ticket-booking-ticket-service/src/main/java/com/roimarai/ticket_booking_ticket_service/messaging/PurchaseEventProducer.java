package com.roimarai.ticket_booking_ticket_service.messaging;

import com.roimarai.ticket_booking_shared.messaging.PurchaseRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PurchaseEventProducer {
    private final KafkaTemplate<String, PurchaseRequestedEvent> kafkaTemplate;

    public PurchaseEventProducer(KafkaTemplate<String, PurchaseRequestedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPurchaseRequested(PurchaseRequestedEvent event) {
        kafkaTemplate.send("ticket-purchase-requested", event.paymentToken(), event);
    }
}
