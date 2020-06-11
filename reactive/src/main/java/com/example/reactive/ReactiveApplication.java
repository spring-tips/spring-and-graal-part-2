package com.example.reactive;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication(proxyBeanMethods = false)
public class ReactiveApplication {

    @Bean
    RouterFunction<ServerResponse> http(CustomerRepository customerRepository) {
        return route()
                .GET("/customers", r -> ServerResponse.ok().body(customerRepository.findAll(), Customer.class))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }
}

@Component
@RequiredArgsConstructor
class Initializer implements ApplicationListener<ApplicationReadyEvent> {

    private final DatabaseClient client;
    private final CustomerRepository customerRepository;

    @Override
    @SneakyThrows
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {


        String sql = "create table customer\n" +
                "(\n" +
                "    id   serial       not null primary key,\n" +
                "    name varchar(255) not null\n" +
                ");";
        this.client
                .execute(sql).fetch().rowsUpdated()
                .thenMany(Flux.just("A", "B", "C").map(name -> new Customer(null, name)).flatMap(this.customerRepository::save))
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