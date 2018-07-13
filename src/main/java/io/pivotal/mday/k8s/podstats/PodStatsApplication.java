package io.pivotal.mday.k8s.podstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PodStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PodStatsApplication.class, args);
	}
}
