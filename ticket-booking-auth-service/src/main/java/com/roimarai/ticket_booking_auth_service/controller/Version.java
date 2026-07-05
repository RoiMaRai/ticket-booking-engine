package com.roimarai.ticket_booking_auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class Version {

    @GetMapping("/version")
    public String getVersion(){
        return "v0.0.1";
    }
}
