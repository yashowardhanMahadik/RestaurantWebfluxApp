package com.ym.restaurantservice.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ym.restaurantservice.exception.ResourceNotFoundException;
import com.ym.restaurantservice.model.Restaurant;
import com.ym.restaurantservice.service.RestaurantService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class RestaurantController {

    @Autowired
    private RestaurantService resServ;

    @PostMapping("/restaurant")
    private Mono<ResponseEntity<Restaurant>> saveRestaurant(@RequestBody Restaurant res){
        return resServ.addRestaurant(res)
                .map(restaurant->{
                    return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Save is not working now , please try after sometime!!"))).log();
    }

    @GetMapping("/restaurant")
    private Mono<ResponseEntity<List<Restaurant>>> findAllRestaurants(){
        return resServ.getAllRestaurants()
                .map(restaurant->{
                    return ResponseEntity.ok().body(restaurant);
                });
        //.switchIfEmpty(Mono.error(new ResourceNotFoundException("Unable to show the Restaurants , Please try after sometime !!"))).log();
    }

    //this endpoint will return the list of restaurants based on restaurantName
    @GetMapping("/restaurant/name/{name}")
    private Mono<ResponseEntity<Restaurant>> fetchRestaurantByName(@PathVariable("name")String name){
        return resServ.getRestaurantByName(name)
                .map(restaurant->{
                    return ResponseEntity.ok().body(restaurant);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("There is no restaruant available with the name :: "+name))).log();
    }

    //this endpoint will return the list of restaurants if the tables are available
    @GetMapping("/restaurant/table")
    private Mono<ResponseEntity<List<Restaurant>>> fetchRestaurantByTable(){
        return resServ.getRestaurantTableAvailable()
                .map(restaurant->{
                    return ResponseEntity.ok().body(restaurant);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("There is no restaurants will available with table you provided!"))).log();
    }

    @PutMapping("/restaurant/{id}")
    private Mono<ResponseEntity<Restaurant>> updateRestaurant(@RequestBody Restaurant restaurant , @PathVariable("id") String id){
        return resServ.updateRestaurant(restaurant, id)
                .map(res->{
                    return ResponseEntity.ok().body(res);
                }).log();
    }

    @DeleteMapping("/restaurant/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private Mono<Void> deleteRestaurant(@PathVariable("id")String id){
        return resServ.deleteRestaurant(id)
                .log();
    }
}
