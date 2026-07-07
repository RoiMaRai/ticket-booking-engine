package com.roimarai.ticket_booking_auth_service.repository;

import com.roimarai.ticket_booking_auth_service.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
}
