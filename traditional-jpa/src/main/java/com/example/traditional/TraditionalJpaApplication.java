package com.example.traditional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;

@SpringBootApplication(
    proxyBeanMethods = false,
    exclude = SpringDataWebAutoConfiguration.class
)
public class TraditionalJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraditionalJpaApplication.class, args);
    }
}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    Collection<Customer> all() {
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