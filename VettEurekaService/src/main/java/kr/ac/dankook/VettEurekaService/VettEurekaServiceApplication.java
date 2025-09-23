package kr.ac.dankook.VettEurekaService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class VettEurekaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettEurekaServiceApplication.class, args);
	}

}
