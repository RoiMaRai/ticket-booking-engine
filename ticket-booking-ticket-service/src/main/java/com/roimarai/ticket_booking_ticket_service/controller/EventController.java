package com.roimarai.ticket_booking_ticket_service.controller;

import com.roimarai.ticket_booking_ticket_service.exception.NotEnoughSeatsException;
import com.roimarai.ticket_booking_ticket_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_ticket_service.model.Event;
import com.roimarai.ticket_booking_ticket_service.model.Reservation;
import com.roimarai.ticket_booking_ticket_service.repository.EventRepository;
import com.roimarai.ticket_booking_ticket_service.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    public EventController(EventRepository eventRepository, ReservationRepository reservationRepository){
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    @GetMapping("/events/pricesum")
    public BigDecimal getSumOfPrice(){
        BigDecimal sum = BigDecimal.ZERO;
        for (Event event : eventRepository.findAll()){
            BigDecimal price = event.getPrice();
            sum = sum.add(price);
        }
        return sum;
    }

    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody Event event){
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @GetMapping("/events/{id}")
    public Event getEvent(@PathVariable("id") Long id){
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()){
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        return event.get();
    }

    @PostMapping("/events/{id}/reserve")
    public ResponseEntity<Reservation> reserveTicketForEvent(@PathVariable("id") Long id, @RequestBody Reservation reservation){
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()){
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        Event currentEvent = event.get();

        if (reservation.getSeatCount() <= currentEvent.getAvailableSeats()){
            // Enough seat
            currentEvent.setAvailableSeats(
                    currentEvent.getAvailableSeats() - reservation.getSeatCount()
            );
            reservation.setEventId(currentEvent.getId());
            reservation.setStatus("RESERVED");
            reservationRepository.save(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        }else{
            // Not enough seat
            String errorMsg = "Could not reserve "+reservation.getSeatCount()+" seats." +
                    "Only "+currentEvent.getAvailableSeats()+" seats are remaining.";
            throw new NotEnoughSeatsException(errorMsg);
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id){
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()){
            throw new ResourceNotFoundException("Event not found with id:"+id);
        }
        else
        {
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}