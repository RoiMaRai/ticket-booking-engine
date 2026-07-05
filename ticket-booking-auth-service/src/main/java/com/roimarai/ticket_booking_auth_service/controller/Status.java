package com.roimarai.ticket_booking_auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Status {

    @GetMapping("/status")
    public String getStatus(){
        return "Auth Service is running";
    }
}
