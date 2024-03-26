package com.ym.bookingservice.controller;

import com.ym.bookingservice.exception.PaymentFailException;
import com.ym.bookingservice.exception.ResourceNotFoundException;
import com.ym.bookingservice.model.Booking;
import com.ym.bookingservice.model.Payment;
import com.ym.bookingservice.services.BookingService;
import com.ym.bookingservice.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class BookingController {
	
	@Autowired
	private BookingService bookServ;

	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/booking/id/{id}/resName/{name}")
	public Mono<ResponseEntity<Booking>> bookingOperation(@PathVariable("id")String id,@PathVariable("name")String name){
		// Record the payment before completing the booking
		return paymentService.processPayment(new Payment("1","1",7000)).flatMap(status -> {
			if(status)
				return bookServ.createBooking(id, name);
			else
				return Mono.error(new PaymentFailException("Payment failed !"));
				})
						.map(book->{
							return ResponseEntity.status(HttpStatus.CREATED).body(book);
						})
						.switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking is not Available now , Please try After Some time!!")));
	}
	
	
	@GetMapping("/booking")
	public Mono<ResponseEntity<List<Booking>>> getAllBookingsList(){
		return bookServ.getAllBookings()
						.map(booking->{
							return ResponseEntity.ok().body(booking);
						}).switchIfEmpty(Mono.error(new ResourceNotFoundException("Unable to Show the bookings / there are no bookings are availble")));
	}
	
	@GetMapping("/booking/id/{id}")
	public Mono<ResponseEntity<Booking>> getBookingById(@PathVariable("id")String id){
		return bookServ.getBookingById(id)
						.map(booking->{
							return ResponseEntity.ok().body(booking);
						}).switchIfEmpty(Mono.error(new ResourceNotFoundException("There is no booking available for given booking id :: "+id)));							
	}
	
	@DeleteMapping("/booking/id/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> cancelBooking(@PathVariable("id")String id){
		return bookServ.deleteBooking(id)
							//.switchIfEmpty(Mono.error(new ResourceNotFoundException("Unable to Delete the Booking for given id ::"+id)))
							.log();
	}
	
	
}
