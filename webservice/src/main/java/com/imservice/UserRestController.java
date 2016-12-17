package com.imservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.model.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserRestController {
	
	private final UserRepository userRepository;

	private final AtomicLong counter = new AtomicLong();

	@Autowired
	UserRestController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/greeting")
	public Greeting greeting() {
		return new Greeting(counter.incrementAndGet(), "Greetings!");
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/login/{username}/{password}")
	public ResponseEntity<?> login(@PathVariable String username, @PathVariable String password) {
		Optional<User> user = this.userRepository.findByUsername(username);
		if (user.isPresent()) {
			if (user.get().getPassword().equals(password)) {
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public ResponseEntity<?> loginWithParam(@RequestParam String username, @RequestParam String password) {
		return login(username, password);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/user/{userId}/role")
	public Role getRole(@PathVariable String userId) {
		return validateUser(userId).getRole();	
	}
	
	private User validateUser(String userId) {
		return this.userRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}
	
}

