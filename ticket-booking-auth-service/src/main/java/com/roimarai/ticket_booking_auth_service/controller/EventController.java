package com.roimarai.ticket_booking_auth_service.controller;

import com.roimarai.ticket_booking_auth_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_auth_service.model.Event;
import com.roimarai.ticket_booking_auth_service.repository.EventRepository;
import org.apache.coyote.Response;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
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
    public Event createEvent(@RequestBody Event event){
        return eventRepository.save(event);
    }

    @GetMapping("/events/{id}")
    public Optional<Event> getEvent(@PathVariable("id") Long id){
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()){
            throw new ResourceNotFoundException("Event not found with id:"+id);
        }
        else{
            return event;
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable("id") Long id){
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