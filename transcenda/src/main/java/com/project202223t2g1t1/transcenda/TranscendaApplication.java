package com.project202223t2g1t1.transcenda;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@RequestMapping(path="/api/v1/transcenda")
public class TranscendaApplication {
	public static void main(String[] args) {
		SpringApplication.run(TranscendaApplication.class, args);
	}

	// health check
	@GetMapping(path="/health")
	public ResponseEntity<String> healthCheck(){
		return ResponseEntity.ok("alive");
	}
}
