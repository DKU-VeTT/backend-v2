package kr.ac.dankook.VettPlaceService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VettPlaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettPlaceServiceApplication.class, args);
	}

}
