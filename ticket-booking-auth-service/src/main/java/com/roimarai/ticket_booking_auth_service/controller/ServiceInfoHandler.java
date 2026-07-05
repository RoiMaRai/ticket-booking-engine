package com.roimarai.ticket_booking_auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceInfoHandler {
    @GetMapping("/info")
    public ServiceInfo getServiceInfo(){
        ServiceInfo serviceInfo = new ServiceInfo();
        return serviceInfo;
    }
}
