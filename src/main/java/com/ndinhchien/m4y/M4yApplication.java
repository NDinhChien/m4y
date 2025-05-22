package com.ndinhchien.m4y;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class M4yApplication {

	public static void main(String[] args) {
		SpringApplication.run(M4yApplication.class, args);
	}

}
