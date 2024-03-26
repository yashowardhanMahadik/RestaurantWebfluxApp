package com.ym.bookingservice.integration.test;

import com.ym.bookingservice.clients.RestaurantRestClient;
import com.ym.bookingservice.clients.UserRestClient;
import com.ym.bookingservice.controller.BookingController;
import com.ym.bookingservice.model.*;
import com.ym.bookingservice.repository.BookingRepository;
import com.ym.bookingservice.services.BookingService;
import com.ym.bookingservice.services.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@WebFluxTest(BookingController.class)
@Import({BookingService.class, PaymentService.class})
public class BookingControllerTest {
	@Autowired
	WebTestClient webTestClient;

	@MockBean
	private UserRestClient userClient;
	@MockBean
	private RestaurantRestClient restaurantClient;
//	@MockBean
//	BookingService bookingService;

	@MockBean
	BookingRepository bookingRepository;


	private final String FULL_URL = "http://localhost:8082/v1/booking";

	@Test
	public void testCreateBooking(){
		Booking booking1 = Booking.builder().id("1")
				.userDetails(new User("a", "Yashow", new Address("bangalore", "karnataka", Long.valueOf(58955)), Long.valueOf(978465455)))
				.restDetails(Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build())
				.build();
		Restaurant restaurant1 = Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build();
		User user1 = new User("1", "Yashow", new Address("bangalore", "karnataka", Long.valueOf(58955)), Long.valueOf(978465455));
		Mockito.when(userClient.retrieveUserData(anyString())).thenReturn(Mono.just(user1));
		Mockito.when(restaurantClient.retrieveRestData(anyString())).thenReturn(Mono.just(restaurant1));
		Mockito.when(bookingRepository.save(Mockito.isA(Booking.class))).thenReturn(Mono.just(booking1));
//		Mockito.when(bookingService.performBookingOperation(anyString(), anyString()).thenReturn(booking1));

		webTestClient.post()
				.uri(FULL_URL+"/id/{id}/resName/{name}","1","Truffles")
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody(Booking.class)
				.consumeWith(bookingEntityExchangeResult -> {
					Booking booking = bookingEntityExchangeResult.getResponseBody();
					Assertions.assertEquals(booking.getRestDetails().getRestaurantName(),"Truffles");
				});
	}

	@Test
	public void testGetAllBookings() throws Exception{
		List<Booking> bookingList = List.of(
				Booking.builder().id("1")
						.userDetails(new User("a", "Yashow", new Address("bangalore", "karnataka", Long.valueOf(58955)), Long.valueOf(978465455)))
						.restDetails(Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build())
						.build(),
				Booking.builder().id("2")
						.userDetails(new User("a", "Aman", new Address("bangalore", "karnataka", Long.valueOf(58955)), Long.valueOf(978465455)))
						.restDetails(Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build())
						.build()
		);
//		Mockito.when(bookingService.getAllBookings()).thenReturn(Mono.just(bookingList));
		Mockito.when(bookingRepository.findAll()).thenReturn(Flux.fromIterable(bookingList));

		webTestClient.get()
				.uri(FULL_URL)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(Booking.class)
				.hasSize(2);
	}

	@Test
	public void testBookingById() throws Exception{
		Booking booking1 = Booking.builder().id("1")
				.userDetails(new User("a", "Yashow", new Address("bangalore", "karnataka", Long.valueOf(58955)), Long.valueOf(978465455)))
				.restDetails(Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build())
				.build();

		Mockito.when(bookingRepository.findById(anyString())).thenReturn(Mono.just(booking1));

		webTestClient.get()
				.uri(FULL_URL + "/id/1")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(Booking.class)
				.consumeWith(bookingEntityExchangeResult -> {
					Booking booking = bookingEntityExchangeResult.getResponseBody();
					Assertions.assertEquals(booking.getId(), "1");
				});
	}

	@Test
	public void testDeleteBooking() throws Exception{
		Mockito.when(bookingRepository.deleteById(anyString())).thenReturn(Mono.empty());

		webTestClient.delete()
				.uri(FULL_URL+"/id/2")
				.exchange()
				.expectStatus()
				.isNoContent();
	}
}