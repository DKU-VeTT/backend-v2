package kr.ac.dankook.VettObservabilityService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VettObservabilityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettObservabilityServiceApplication.class, args);
	}

}
