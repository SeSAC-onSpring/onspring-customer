package com.onspring.onspring_customer;

import com.onspring.onspring_customer.global.dotenv.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OnspringCustomerApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(OnspringCustomerApplication.class);
		application.addInitializers(new DotenvInitializer());
		application.run(args);
	}
}


