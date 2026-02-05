package com.smart.ecommerce;

import com.smart.ecommerce.repository.ReviewRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EntityScan("com.smart.ecommerce.model")
@EnableMongoRepositories(
        basePackageClasses = ReviewRepository.class
)
@EnableCaching
public class EcommerceApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue() ));
		SpringApplication.run(EcommerceApplication.class, args);

	}


}
