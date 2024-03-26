package com.ym.userservice.handler;

import com.ym.userservice.exception.UserDataException;
import com.ym.userservice.exception.UserNotFoundException;
import com.ym.userservice.model.User;
import com.ym.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

	@Autowired
	private UserRepository userRepo;
	
	public Mono<ServerResponse> addUser(ServerRequest req){
		return req.bodyToMono(User.class)
					.flatMap(userRepo::save)
					.flatMap(user->
							ServerResponse.status(HttpStatus.CREATED)
											.bodyValue(user))
					.switchIfEmpty(Mono.error(new UserDataException("Unable to Save the Data !! Due to Some technical Issue.")));
	}
	
	public Mono<ServerResponse> getAllUsers(ServerRequest req){
		var userFlux = userRepo.findAll();
		return ServerResponse.ok().body(userFlux,User.class)
					.switchIfEmpty(Mono.error(new UserDataException("Server is Stuborn !! Please try again later.")));
	}
	
	public Mono<ServerResponse> getUserById(ServerRequest req){
		var userId = req.pathVariable("id");
		var userFlux = userRepo.findById(userId)
							.switchIfEmpty(Mono.error(new UserNotFoundException("There is no data Found with user ID :: "+userId)));
		return ServerResponse.ok().body(userFlux,User.class);
				
	}
	
	public Mono<ServerResponse> updateUser(ServerRequest req){
		var userId = req.pathVariable("id");
		var userFlux = userRepo.findById(userId).switchIfEmpty(Mono.error(new UserNotFoundException("There is no data Found with user ID :: "+userId)));
		return userFlux
					.flatMap(user->
								req.bodyToMono(User.class)
									.map(u->{
										user.setName(u.getName());
										user.setPhoneNumber(u.getPhoneNumber());
										user.setAddress(u.getAddress());
										return user;
									})
									.flatMap(userRepo::save)
									.flatMap(updateUser->ServerResponse.ok().bodyValue(updateUser)))
									.switchIfEmpty(Mono.error(new UserDataException("Unable to update the Data !! Due to Some technical Issue.")));
	}
	
	public Mono<ServerResponse> deleteUser(ServerRequest req){
		var userId = req.pathVariable("id");
		var user = userRepo.findById(userId).switchIfEmpty(Mono.error(new UserNotFoundException("There is no data Found with user ID :: "+userId)));
		return user.flatMap(u->userRepo.deleteById(userId)
										.then(ServerResponse.noContent().build()));
	}
}
