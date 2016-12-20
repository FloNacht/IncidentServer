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

/**
* <h1>Spring RestController</h1>
* This class serves as a rest controller mapping the http calls to java methods.
* The methods call reading or writing operations on the database.
*
* Handles all user related http calls.
* 
* @author  Florian Nachtigall
* @version 1.0
* @since   2016-12-20
*/
@RestController
public class UserRestController {
	
	private final UserRepository userRepository;

	private final AtomicLong counter = new AtomicLong();

	@Autowired
	UserRestController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * This method is used to test the rest api and the accessibility of the server.
	 * @return Greeting - Test Object  for this use case only
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/greeting")
	public Greeting greeting() {
		return new Greeting(counter.incrementAndGet(), "Greetings!");
	}
	
	/**
	 * This method is used to authenticate a user and allow to create and view incidents
	 * 
	 * @param username to find the user
	 * @param password to authenticate the user
	 * @return ResponseEntity<?> With status code stating success or failure of login
	 */
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
	
	/**
	 * This method is used to authenticate a user and allow to create and view incidents
	 * 
	 * @param username to find the user
	 * @param password to authenticate the user
	 * @return ResponseEntity<?> With status code stating success or failure of login
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public ResponseEntity<?> loginWithParam(@RequestParam String username, @RequestParam String password) {
		return login(username, password);
	}
	
	/**
	 * This method is used to get the role of a user in order to authorize him/her for certain operations
	 * 
	 * @param userId to find the user
	 * @return Role Defining users' rights
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/user/{userId}/role")
	public Role getRole(@PathVariable String userId) {
		return validateUser(userId).getRole();	
	}
	
	private User validateUser(String userId) {
		return this.userRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}
	
}

