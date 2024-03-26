package com.ym.restaurantservice.service;

import java.util.List;
import java.util.stream.Collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ym.restaurantservice.model.BookTable;
import com.ym.restaurantservice.model.Restaurant;
import com.ym.restaurantservice.repository.RestaurantRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository resRepo;


    public Mono<Restaurant> addRestaurant(Restaurant res){
        return resRepo.save(res);
    }

    public Mono<List<Restaurant>> getAllRestaurants(){
        return resRepo.findAll().collectList();
    }

    public Mono<Restaurant> updateRestaurant(Restaurant res , String id){
        List<BookTable> book = res.getTables();
        return resRepo.findById(id)
                .flatMap(r->{
                    r.setLocation(res.getLocation());
                    r.setRestaurantName(res.getRestaurantName());
                    r.setType(res.getType());
                    r.setTables(book);
                    return resRepo.save(r);
                });
    }

    public Mono<Restaurant> getRestaurantByName(String name){
        return resRepo.findByRestaurantName(name);
    }

    public Flux<Restaurant> getRestaurantByLocation(String location){
        return resRepo.findByLocation(location);
    }

    public Mono<List<Restaurant>> getRestaurantTableAvailable(){
        return resRepo.findAll()
                .filter(res->
                        res.getTables().stream()
                                .anyMatch(t->t.getIsAvailable()==true))
                .collectList().log();
    }

    public Mono<Void> deleteRestaurant(String id){
        return resRepo.deleteById(id);
    }
}
