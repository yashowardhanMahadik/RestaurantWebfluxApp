package com.ym.userservice.unit.test;

import com.ym.userservice.handler.UserHandler;
import com.ym.userservice.model.Address;
import com.ym.userservice.model.User;
import com.ym.userservice.repository.UserRepository;
import com.ym.userservice.router.UserRouter;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserRouter.class)
@Import(UserHandler.class)
public class UserControllerUnitTest {
//	@Autowired
//	private TestRestTemplate restTemplate;

	@Autowired
	private WebTestClient webTestClient;

	static String USER_FULL_URL = "http://localhost:8081/v1/user";

	@MockBean
	private UserRepository userRepository;


	@Test
	public void testAddUser() throws Exception {
		User user1 = new User("11", "Yashow", new Address("Bengaluru", "Karnataka", Long
				.valueOf(12313)), Long.valueOf(999999999));
//		ResponseEntity<String> response = restTemplate.getForEntity(
//				new URL("http://localhost:" + "8080" + "/").toString(), String.class);
//		assertEquals("Hello Controller", response.getBody());//
////		webTestClient.post().uri("http://localhost:8081/v1/user")
////								.bodyValue(user1)
////								.exchange().expectStatus().isCreated();
//
//		webTestClient.post()
//						.uri("/v1/user")
//								.contentType(MediaType.APPLICATION_JSON)
//										.body(BodyInserters.fromObject(user1))
//												.exchange()
//														.expectStatus().isCreated();
		Mockito.when(userRepository.save(Mockito.isA(User.class))).thenReturn(Mono.just(user1));

		webTestClient
				.post()
				.uri(USER_FULL_URL)
				.bodyValue(user1)
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody(User.class)
				.consumeWith(u->{
					var savedUser = u.getResponseBody();
					assert savedUser!=null;
					assertNotNull(savedUser.getId());
				});

		Mockito.verify(userRepository,Mockito.times(1)).save(user1);
	}

	@Test
	public void testGetALlUsers() throws Exception {
		Address address1 = new Address("Bengaluru", "Karnataka", Long
				.valueOf(12313));
		var userList = List.of(
				User.builder().id("12").name("Aditya").address(address1).phoneNumber(Long.valueOf(999999999)).build(),
				User.builder().id("13").name("Aman").address(address1).phoneNumber(Long.valueOf(999999999)).build(),
				User.builder().id("14").name("Kaustubh").address(address1).phoneNumber(Long.valueOf(999999999)).build(),
				User.builder().id("15").name("Sunil").address(address1).phoneNumber(Long.valueOf(999999999)).build()
		);
		Mockito.when(userRepository.findAll()).thenReturn(Flux.fromIterable(userList));

		webTestClient.get()
				.uri(USER_FULL_URL)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(User.class)
				.hasSize(4);
	}

	@Test
	public void testGetUserById() throws Exception{
		User user1 = new User("11", "Yashow", new Address("Bengaluru", "Karnataka", Long
				.valueOf(12313)), Long.valueOf(999999999));
		Mockito.when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));

		webTestClient.get()
				.uri(USER_FULL_URL + "/11")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(User.class)
				.consumeWith(userEntityExchangeResult -> {
					var user = userEntityExchangeResult.getResponseBody();
					assertNotNull(user);
					Assertions.assertEquals(user.getName(), "Yashow");
				});
	}

	@Test
	public void testUpdateUser() throws Exception{
		User user1 = new User("11", "Yashow", new Address("Bengaluru", "Karnataka", Long
				.valueOf(12313)), Long.valueOf(999999999));
		Address address1 = new Address("Bengaluru", "Karnataka", Long
				.valueOf(12313));
		User user2 = User.builder().id("11").name("Aditya").address(address1).phoneNumber(Long.valueOf(999999999)).build();
		Mockito.when(userRepository.findById(Mockito.isA(String.class)))
				.thenReturn(Mono.just(user1));
		Mockito.when(userRepository.save(Mockito.isA(User.class))).thenReturn(Mono.just(user2));

		webTestClient.put()
				.uri(USER_FULL_URL+"/11")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(user1)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(User.class)
				.consumeWith(userEntityExchangeResult -> {
					var userUpdated = userEntityExchangeResult.getResponseBody();
					Assertions.assertEquals(userUpdated.getName(),"Aditya");
				});
	}

	@Test
	public void testDeleteUser() throws Exception{
		Address address1 = new Address("Bengaluru", "Karnataka", Long
				.valueOf(12313));
		User user2 = User.builder().id("11").name("Aditya").address(address1).phoneNumber(Long.valueOf(999999999)).build();
		Mockito.when(userRepository.findById(Mockito.isA(String.class)))
				.thenReturn(Mono.just(user2));
		Mockito.when(userRepository.deleteById(anyString())).thenReturn(Mono.empty());

		webTestClient.delete()
				.uri(USER_FULL_URL + "/11")
				.exchange()
				.expectStatus()
				.isNoContent();
	}
}
