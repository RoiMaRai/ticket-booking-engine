package com.roimarai.ticket_booking_auth_service.controller;

import com.roimarai.ticket_booking_auth_service.model.Event;
import com.roimarai.ticket_booking_auth_service.repository.EventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/pricesum")
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
}