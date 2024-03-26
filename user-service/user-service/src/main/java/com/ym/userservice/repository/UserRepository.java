package com.ym.userservice.repository;

import com.ym.userservice.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String>{

}
