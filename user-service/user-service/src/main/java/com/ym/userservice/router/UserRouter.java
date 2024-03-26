package com.ym.userservice.router;

import com.ym.userservice.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

	@Bean
	public RouterFunction<ServerResponse> usersRoute(UserHandler userHandler){

		return route()
				.nest(path("/v1/user"), builder->
						builder
							.POST("",userHandler::addUser)
							.GET("/{id}",userHandler::getUserById)
							.GET("",userHandler::getAllUsers)
							.PUT("/{id}",userHandler::updateUser)
							.DELETE("/{id}",userHandler::deleteUser))
							
				.build();
				
	}
}
