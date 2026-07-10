package com.roimarai.ticket_booking_auth_service.repository;

import com.roimarai.ticket_booking_auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
