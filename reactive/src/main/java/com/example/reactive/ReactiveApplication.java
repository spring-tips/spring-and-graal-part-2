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
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.io.InputStreamReader;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication(
        exclude = SpringDataWebAutoConfiguration.class,
        proxyBeanMethods = false
)
public class ReactiveApplication {

    @Bean
    RouterFunction<ServerResponse> http(CustomerRepository customerRepository) {
        return route()
                .GET("/customers", r -> ok().body(customerRepository.findAll(), Customer.class))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }

}


@Component
@RequiredArgsConstructor
class CustomerInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomerRepository customerRepository;
    private final DatabaseClient databaseClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent are) {
        try {
            Resource resource = new ClassPathResource("schema.sql");
            try (InputStreamReader in = new InputStreamReader(resource.getInputStream())) {
                String sql = FileCopyUtils.copyToString(in);
                databaseClient
                        .execute(sql)
                        .fetch()
                        .rowsUpdated()
                        .thenMany(
                                Flux
                                        .just("Violetta", "Madhura")
                                        .map(name -> new Customer(null, name))
                                        .flatMap(this.customerRepository::save))
                        .subscribe(System.out::println);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }


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