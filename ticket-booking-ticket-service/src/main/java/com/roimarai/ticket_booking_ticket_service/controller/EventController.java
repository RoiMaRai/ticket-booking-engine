package com.roimarai.ticket_booking_ticket_service.controller;

import com.roimarai.ticket_booking_ticket_service.exception.NotEnoughSeatsException;
import com.roimarai.ticket_booking_ticket_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_ticket_service.messaging.PurchaseEventProducer;
import com.roimarai.ticket_booking_ticket_service.messaging.PurchaseRequestedEvent;
import com.roimarai.ticket_booking_ticket_service.model.Event;
import com.roimarai.ticket_booking_ticket_service.model.Reservation;
import com.roimarai.ticket_booking_ticket_service.dto.ReservationRequest;
import com.roimarai.ticket_booking_ticket_service.dto.ReservationResult;
import com.roimarai.ticket_booking_ticket_service.repository.EventRepository;
import com.roimarai.ticket_booking_ticket_service.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@RestController
public class EventController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final PurchaseEventProducer purchaseEventProducer;
    public EventController(EventRepository eventRepository, ReservationRepository reservationRepository, PurchaseEventProducer purchaseEventProducer){
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.purchaseEventProducer = purchaseEventProducer;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations(){
        return reservationRepository.findAll();
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
        String key = "event:" + id;

        Object cachedEvent = redisTemplate.opsForValue().get(key);
        if (cachedEvent != null){
            return (Event) cachedEvent;
        }else{
            Optional<Event> event = eventRepository.findById(id);
            if (event.isEmpty()){
                throw new ResourceNotFoundException("Event not found with id: " + id);
            }
            redisTemplate.opsForValue().set(key,event.get());
            return event.get();
        }
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

    @PostMapping("/events/{id}/reserve")
    public ResponseEntity<Reservation> reserve(@PathVariable("id") Long id, @RequestBody ReservationRequest request){
        ReservationResult result = reserveTicketForEvent(id, request);

        purchaseEventProducer.publishPurchaseRequested(new PurchaseRequestedEvent(
                result.reservation().getId(),
                request.recipient(),
                request.paymentToken(),
                result.totalPrice()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(result.reservation());
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