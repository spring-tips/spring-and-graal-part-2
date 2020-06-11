package com.example.traditional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.stream.Stream;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication(
        proxyBeanMethods = false,
        exclude = SpringDataWebAutoConfiguration.class)
public class TraditionalJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraditionalJpaApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> http(CustomerRepository repository) {
        return route()
                .GET("/customers", r -> ServerResponse.ok().body(repository.findAll()))
                .build();
    }

}

@Component
@RequiredArgsConstructor
class Initializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomerRepository repository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        Stream
                .of("A", "B", "C")
                .map(name -> new Customer(null, name))
                .map(this.repository::save)
                .forEach(System.out::println);
    }
}


interface CustomerRepository extends JpaRepository<Customer, Integer> {
}

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}