package com.imservice;

import java.util.Arrays;

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
	

//	@Bean
//	CommandLineRunner init(UserRepository userRepository,
//			IncidentRepository incidentRepository)  {
//		return (evt) -> Arrays.asList(
//				"jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
//				.forEach(
//						a -> {
//							User user = userRepository.save(new User(a,
//									"password"));
//							incidentRepository.save(new Incident(user,
//									"http://bookmark.com/1/" + a,"exact Location", "A description", "image dir"));
//							incidentRepository.save(new Incident(user,
//									"http://bookmark.com/2/" + a, "exact Location", "A description", "image dir"));
//						});
//	}	

//	 @Override
//     protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//         return application.sources(WebserviceApplication.class);
//     }
}
