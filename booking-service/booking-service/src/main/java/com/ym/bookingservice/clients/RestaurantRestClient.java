package com.ym.bookingservice.clients;

import com.ym.bookingservice.model.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RestaurantRestClient {

	@Autowired
	private WebClient wclient;

	@Value("${rest.client.restaurant.url}")
	private String restaurantUrl;

	public Mono<Restaurant> retrieveRestData(String resId){

		var url = restaurantUrl.concat("/name/{name}");

		return wclient
				.get()
				.uri(url,resId)
				.retrieve()
				.bodyToMono(Restaurant.class)
				.log();
	}
}
