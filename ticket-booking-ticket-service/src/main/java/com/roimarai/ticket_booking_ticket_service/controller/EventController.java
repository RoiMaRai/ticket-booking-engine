package com.roimarai.ticket_booking_ticket_service.controller;

import com.roimarai.ticket_booking_ticket_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_ticket_service.messaging.PurchaseEventProducer;
import com.roimarai.ticket_booking_shared.messaging.PurchaseRequestedEvent;
import com.roimarai.ticket_booking_ticket_service.model.Event;
import com.roimarai.ticket_booking_ticket_service.model.Reservation;
import com.roimarai.ticket_booking_ticket_service.dto.ReservationRequest;
import com.roimarai.ticket_booking_ticket_service.dto.ReservationResult;
import com.roimarai.ticket_booking_ticket_service.repository.EventRepository;
import com.roimarai.ticket_booking_ticket_service.repository.ReservationRepository;

import com.roimarai.ticket_booking_ticket_service.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;

@RestController
public class EventController {
    private final ReservationService reservationService;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final PurchaseEventProducer purchaseEventProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    public EventController(EventRepository eventRepository,
                           ReservationRepository reservationRepository,
                           PurchaseEventProducer purchaseEventProducer,
                           ReservationService reservationService,
                           RedisTemplate<String, Object> redisTemplate
    ){
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.purchaseEventProducer = purchaseEventProducer;
        this.reservationService = reservationService;
        this.redisTemplate = redisTemplate;
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

    @PostMapping("/events/{id}/reserve")
    public ResponseEntity<Reservation> reserve(@PathVariable("id") Long id, @RequestBody ReservationRequest request){
        ReservationResult result = reservationService.reserveTicketForEvent(id, request);

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