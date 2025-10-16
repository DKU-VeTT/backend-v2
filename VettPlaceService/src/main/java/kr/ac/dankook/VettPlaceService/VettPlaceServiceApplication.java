package kr.ac.dankook.VettPlaceService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
public class VettPlaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettPlaceServiceApplication.class, args);
	}

}
