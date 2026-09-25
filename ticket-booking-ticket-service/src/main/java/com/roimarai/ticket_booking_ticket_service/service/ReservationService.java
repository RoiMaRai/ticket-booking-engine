package com.roimarai.ticket_booking_ticket_service.service;

import com.roimarai.ticket_booking_ticket_service.dto.ReservationRequest;
import com.roimarai.ticket_booking_ticket_service.dto.ReservationResult;
import com.roimarai.ticket_booking_ticket_service.exception.NotEnoughSeatsException;
import com.roimarai.ticket_booking_ticket_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_ticket_service.model.Event;
import com.roimarai.ticket_booking_ticket_service.model.Reservation;
import com.roimarai.ticket_booking_ticket_service.repository.EventRepository;
import com.roimarai.ticket_booking_ticket_service.repository.ReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ReservationService {
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ReservationService(EventRepository eventRepository,
                              ReservationRepository reservationRepository,
                              RedisTemplate<String, Object> redisTemplate
    ){
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public ReservationResult reserveTicketForEvent(Long id, ReservationRequest request){
        Optional<Event> event = eventRepository.findWithLockById(id);
        if (event.isEmpty()){
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        Event currentEvent = event.get();

        if (request.seatCount() <= currentEvent.getAvailableSeats()){
            // Enough seat
            currentEvent.setAvailableSeats(
                    currentEvent.getAvailableSeats() - request.seatCount()
            );
            eventRepository.save(currentEvent);

            Reservation reservation = new Reservation();
            reservation.setEventId(currentEvent.getId());
            reservation.setSeatCount(request.seatCount());
            reservation.setStatus("RESERVED");
            reservationRepository.save(reservation);

            String key = "event:" + id;
            redisTemplate.delete(key);

            BigDecimal totalPrice = currentEvent.getPrice().multiply(BigDecimal.valueOf(request.seatCount()));
            return new ReservationResult(reservation, totalPrice);
        }else{
            // Not enough seat
            String errorMsg = "Could not reserve "+request.seatCount()+" seats." +
                    "Only "+currentEvent.getAvailableSeats()+" seats are remaining.";
            throw new NotEnoughSeatsException(errorMsg);
        }
    }
}
