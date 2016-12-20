package com.imservice;

import java.util.Arrays;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.model.*;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.imservice", "com.model", "com.storage"})
@EnableJpaRepositories(basePackages = {"com.model"})
@EntityScan(basePackages = "com.model")
	public class WebserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebserviceApplication.class, args);
	}
	

	@Bean
	CommandLineRunner init(UserRepository userRepository, IncidentRepository incidentRepository) {
		return (evt) -> Arrays.asList("628123,621278,612893,589231,783242,R.Hartmann,F.Becker,231328,657912".split(","))
				.forEach(a -> {
					
					if (a.matches("[0-9]+")) {
						
						User user = userRepository.save(new User(a, "password"));
						
						Random r = new Random();
						char c = (char)(r.nextInt(26) + 'A');
						
						incidentRepository.save(new Incident(user, "Dreckig","Haus " + c, "Unter der Decke",
								"Hier ist was dreckig", "image dir"));
						incidentRepository.save(new Incident(user, "Kaputt", "Haus " + c, "Heizung",
								"Hier ist was kaputt", "image dir"));
					} else {
						userRepository.save(new User(a, "password", Role.FACULTY));
					}
				});
	}

//	 @Override
//     protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//         return application.sources(WebserviceApplication.class);
//     }
}
