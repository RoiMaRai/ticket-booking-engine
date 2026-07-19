package com.roimarai.ticket_booking_ticket_service.controller;

import com.roimarai.ticket_booking_ticket_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_ticket_service.model.Event;
import com.roimarai.ticket_booking_ticket_service.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {
    private final EventRepository eventRepository;
    public EventController(EventRepository eventRepository){
        this.eventRepository = eventRepository;
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