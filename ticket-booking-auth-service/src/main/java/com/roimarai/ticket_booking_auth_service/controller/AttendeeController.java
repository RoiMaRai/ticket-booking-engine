package com.roimarai.ticket_booking_auth_service.controller;

import com.roimarai.ticket_booking_auth_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_auth_service.model.Attendee;
import com.roimarai.ticket_booking_auth_service.repository.AttendeeRepository;
import com.roimarai.ticket_booking_auth_service.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AttendeeController {
    private final AttendeeRepository attendeeRepository;
    private final EventRepository eventRepository;

    public AttendeeController(AttendeeRepository attendeeRepository, EventRepository eventRepository){
        this.attendeeRepository = attendeeRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/attendees")
    public List<Attendee> getAllAttendees(){
        return attendeeRepository.findAll();
    }

    @PostMapping("/attendees")
    public ResponseEntity<Attendee> addAttendee(@RequestBody Attendee attendee){
        Attendee savedAttendee = attendeeRepository.save(attendee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAttendee);
    }

    @GetMapping("/attendees/{id}")
    public Attendee getAttendee(@PathVariable("id") Long id){
        Optional<Attendee> attendee = attendeeRepository.findById(id);
        if (attendee.isEmpty()){
            throw new ResourceNotFoundException("Attendee not found with id: "+id);
        }else{
            return attendee.get();
        }
    }

    @DeleteMapping("/attendees/{id}")
    public ResponseEntity<Void> deleteAttendee(@PathVariable("id") Long id){
        Optional<Attendee> attendee = attendeeRepository.findById(id);
        if (attendee.isEmpty()){
            throw new ResourceNotFoundException("Attendee not found with id: "+id);
        }
        else{
            attendeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}
