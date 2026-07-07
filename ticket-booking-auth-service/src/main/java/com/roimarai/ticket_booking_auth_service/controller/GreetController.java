package com.roimarai.ticket_booking_auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetController {
    @GetMapping("/greet/{name}")
    public String greet(@PathVariable("name") String name){
        return "Hello "+name;
    }
}
