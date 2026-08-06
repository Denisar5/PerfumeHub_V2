package com.denisar5.perfumehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PerfumehubMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfumehubMainApplication.class, args);
	}

}
