package com.ym.restaurantservice.test;

import com.ym.restaurantservice.handler.RestaurantController;
import com.ym.restaurantservice.model.BookTable;
import com.ym.restaurantservice.model.Restaurant;
import com.ym.restaurantservice.service.RestaurantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = RestaurantController.class)
@Import(RestaurantService.class)
class RestaurantControllerUnitTest {

	@MockBean
	RestaurantService restaurantService;

	@Autowired
	WebTestClient webTestClient;

	private String FULL_URL = "http://localhost:8080/v1/restaurant";

	@Test
	public void testFindAllRestaurant() throws Exception{
		var restaurantList = List.of(
				Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2,false),new BookTable(3,true))).build(),
				Restaurant.builder().id("2").restaurantName("A2b").location("sector1").type("veg").tables(List.of(new BookTable(2,false),new BookTable(3,true))).build(),
				Restaurant.builder().id("3").restaurantName("Udupi").location("sector1").type("veg").tables(List.of(new BookTable(2,false),new BookTable(3,true))).build()
		);
		Mockito.when(restaurantService.getAllRestaurants()).thenReturn(Mono.just(restaurantList));

		webTestClient.get()
				.uri(FULL_URL)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(Restaurant.class)
				.hasSize(3);
	}

	@Test
	public void testSaveRestaurant() throws Exception{
		Restaurant restaurant1 = Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build();

//work		Mockito.when(restaurantRepository.save(Mockito.isA(Restaurant.class))).thenReturn(Mono.just(restaurant1));
		Mockito.when(restaurantService.addRestaurant(Mockito.isA(Restaurant.class))).thenReturn(Mono.just(restaurant1));
		webTestClient.post()
				.uri(FULL_URL)
				.contentType(MediaType.APPLICATION_JSON)
//				.bodyValue(Restaurant.class)
				.body(Mono.just(restaurant1),Restaurant.class)
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody(Restaurant.class)
				.consumeWith(restaurantEntityExchangeResult -> {
					var rest = restaurantEntityExchangeResult.getResponseBody();
					Assertions.assertEquals(rest.getRestaurantName(),"Truffles");
				});
	}

	@Test
	public void testFindRestByName() throws Exception{
		Restaurant restaurant1 = Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build();
		Mockito.when(restaurantService.getRestaurantByName(anyString())).thenReturn(Mono.just(restaurant1));
		webTestClient.get()
				.uri(FULL_URL+"/name/Truffles")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(Restaurant.class)
				.consumeWith(restaurantEntityExchangeResult -> {
					var rest = restaurantEntityExchangeResult.getResponseBody();
					Assertions.assertEquals(rest.getRestaurantName(),"Truffles");
				});
	}

	@Test
	public void testGetByTable() throws Exception{
		var restaurantList = List.of(
				Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2,false),new BookTable(3,true))).build(),
				Restaurant.builder().id("1").restaurantName("A2b").location("sector1").type("veg").tables(List.of(new BookTable(2,false),new BookTable(3,true))).build(),
				Restaurant.builder().id("1").restaurantName("Udupi").location("sector1").type("veg").tables(List.of(new BookTable(2,false),new BookTable(3,false))).build()
		);
		Mockito.when(restaurantService.getRestaurantTableAvailable()).thenReturn(Mono.just(restaurantList));
		webTestClient.get()
				.uri(FULL_URL+"/table")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(Restaurant.class)
				.hasSize(3);
	}

	@Test
	public void testUpdateRestaurant() throws Exception{
		Restaurant restaurant1 = Restaurant.builder().id("1").restaurantName("Truffles").location("sector1").type("veg").tables(List.of(new BookTable(2, false), new BookTable(3, true))).build();
		Mockito.when(restaurantService.updateRestaurant(Mockito.isA(Restaurant.class),anyString()))
						.thenReturn(Mono.just(restaurant1));

		webTestClient.put()
				.uri(FULL_URL+"/1")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(restaurant1),Restaurant.class)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(Restaurant.class)
				.consumeWith(restaurantEntityExchangeResult -> {
					var rest = restaurantEntityExchangeResult.getResponseBody();
					Assertions.assertEquals(rest.getRestaurantName(),"Truffles");
				});
	}

	@Test
	public void testDeleteRestaurant() throws Exception{
		Mockito.when(restaurantService.deleteRestaurant(anyString())).thenReturn(Mono.empty());
			webTestClient.delete()
					.uri(FULL_URL+"/1")
					.exchange()
					.expectStatus()
					.isNoContent();
	}
}