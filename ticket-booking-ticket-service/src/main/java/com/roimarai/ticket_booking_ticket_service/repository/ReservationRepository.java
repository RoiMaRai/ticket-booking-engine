package com.roimarai.ticket_booking_ticket_service.repository;

import com.roimarai.ticket_booking_ticket_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
