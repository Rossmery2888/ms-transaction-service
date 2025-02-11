package com.example.mstransactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsTransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransactionServiceApplication.class, args);
	}

}
