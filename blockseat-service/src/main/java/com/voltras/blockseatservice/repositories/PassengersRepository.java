package com.voltras.blockseatservice.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voltras.blockseatservice.entities.PassengerData;

public interface PassengersRepository extends JpaRepository<PassengerData, UUID>{

	Optional<PassengerData> findByBookingCode (String bookingCode);
}
