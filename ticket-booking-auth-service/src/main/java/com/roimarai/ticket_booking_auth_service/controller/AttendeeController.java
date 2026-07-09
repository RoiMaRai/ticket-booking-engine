package com.roimarai.ticket_booking_auth_service.controller;

import com.roimarai.ticket_booking_auth_service.exception.ResourceNotFoundException;
import com.roimarai.ticket_booking_auth_service.model.Attendee;
import com.roimarai.ticket_booking_auth_service.repository.AttendeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AttendeeController {
    private final AttendeeRepository attendeeRepository;
    AttendeeController(AttendeeRepository attendeeRepository){
        this.attendeeRepository = attendeeRepository;
    }

    @GetMapping("/attendees")
    public List<Attendee> getAllAttendees(){
        return attendeeRepository.findAll();
    }

    @PostMapping("/attendees")
    public Attendee addAttendee(@RequestBody Attendee attendee){
        return attendeeRepository.save(attendee);
    }

    @GetMapping("/attendees/{id}")
    public Optional<Attendee> getAttendee(@PathVariable("id") Long id){
        Optional<Attendee> attendee = attendeeRepository.findById(id);
        if (attendee.isEmpty()){
            throw new ResourceNotFoundException("Attendee not found with id: "+id);
        }else{
            return attendee;
        }
    }

    @DeleteMapping("/attendees/{id}")
    public ResponseEntity<Attendee> deleteAttendee(@PathVariable("id") Long id){
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
