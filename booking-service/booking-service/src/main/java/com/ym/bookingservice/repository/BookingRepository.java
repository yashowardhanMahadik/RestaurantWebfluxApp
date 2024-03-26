package com.ym.bookingservice.repository;

import com.ym.bookingservice.model.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String>{

}
