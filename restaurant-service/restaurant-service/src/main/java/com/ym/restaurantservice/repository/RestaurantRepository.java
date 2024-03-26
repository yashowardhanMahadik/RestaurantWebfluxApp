package com.ym.restaurantservice.repository;

import com.ym.restaurantservice.model.Restaurant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RestaurantRepository extends ReactiveMongoRepository<Restaurant, String>{
	
	public Mono<Restaurant> findByRestaurantName(String name);
	public Flux<Restaurant> findByLocation(String location);
}
