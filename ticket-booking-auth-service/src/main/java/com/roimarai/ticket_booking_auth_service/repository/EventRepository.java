package com.roimarai.ticket_booking_auth_service.repository;

import com.roimarai.ticket_booking_auth_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}