package com.nttdata.bankapp.transaction;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación.
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Customer Service API",
        version = "1.0",
        description = "API para la gestión de clientes del banco"
))

public class MsTransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsTransactionServiceApplication.class, args);
    }
}

