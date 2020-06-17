package com.example.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication(
        exclude = SpringDataWebAutoConfiguration.class,
        proxyBeanMethods = false)
public class JpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }
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

interface CustomerRepository extends JpaRepository<Customer, Integer> {
}

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
}