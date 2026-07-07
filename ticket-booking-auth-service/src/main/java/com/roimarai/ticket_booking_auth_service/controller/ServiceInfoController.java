package com.roimarai.ticket_booking_auth_service.controller;

import com.roimarai.ticket_booking_auth_service.model.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceInfoController {
    @GetMapping("/info")
    public ServiceInfo getServiceInfo(){
        ServiceInfo serviceInfo = new ServiceInfo();
        return serviceInfo;
    }
}
