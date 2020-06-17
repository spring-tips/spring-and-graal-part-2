package com.example.reactive;

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
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication(
        exclude = SpringDataWebAutoConfiguration.class,
        proxyBeanMethods = false
)
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }

}


@RestController
@RequiredArgsConstructor
class CustomerRestController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    Flux<Customer> customers() {
        return this.customerRepository.findAll();
    }
}

@Component
@RequiredArgsConstructor
class Initializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomerRepository customerRepository;

    private final DatabaseClient databaseClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Flux<Customer> save = Flux.just("Madhura", "Dr. Syer")
                .map(name -> new Customer(null, name))
                .flatMap(this.customerRepository::save);

        String sql = "create table CUSTOMER(id serial primary key, name varchar(255))";

        this.databaseClient
                .execute(sql)
                .fetch()
                .rowsUpdated()
                .thenMany(save)
                .thenMany(this.customerRepository.findAll())
                .subscribe(System.out::println);


    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

    @Id
    private Integer id;
    private String name;

}