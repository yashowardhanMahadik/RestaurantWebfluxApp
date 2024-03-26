package com.ym.bookingservice.clients;

import com.ym.bookingservice.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserRestClient {

	@Value("${rest.client.user.url}")
	private String userUrl;
	
	@Autowired
	private WebClient wclient;
	
	public Mono<User> retrieveUserData(String userId){
		var url = userUrl.concat("/{id}");
		
		return wclient
					.get()
					.uri(url,userId)
					.retrieve()
					.bodyToMono(User.class)
					.log();
	}
}
