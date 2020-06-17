package com.example.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication (exclude = SpringDataWebAutoConfiguration.class, proxyBeanMethods = false)
public class MongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongodbApplication.class, args);
	}

}

interface CustomerRepository extends MongoRepository <Customer , String >{}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

	@Id
	private String id;
	private String name;
}



@Component
@RequiredArgsConstructor
class CustomerInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final CustomerRepository customerRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent are) {
		Stream
				.of("Violetta", "Madhura")
				.map(name -> new Customer(null, name))
				.map(this.customerRepository::save)
				.forEach(System.out::println);

	}
}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

	private final CustomerRepository customerRepository;

	@GetMapping("/customers")
	Collection<Customer> customers() {
		return this.customerRepository.findAll();
	}
}
