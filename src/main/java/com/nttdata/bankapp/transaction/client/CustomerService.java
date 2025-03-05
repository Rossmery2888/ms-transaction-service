package com.nttdata.bankapp.transaction.client;

import com.nttdata.bankapp.transaction.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Cliente para comunicarse con el microservicio de clientes.
 */
@Service
@Slf4j
public class CustomerService {

    private final WebClient webClient;

    public CustomerService(@Value("${app.customer-service-url}") String customerServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    /**
     * Verifica si un cliente existe por su ID.
     * @param customerId ID del cliente
     * @return Mono true si existe, false en caso contrario
     */
    public Mono<Boolean> customerExists(String customerId) {
        log.info("Checking if customer exists with id: {}", customerId);
        return webClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(e -> {
                    log.error("Error checking customer existence: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Obtiene todos los clientes.
     * @return Flux de TransactionDto
     */
    public Flux<TransactionDto> findAll() {
        log.info("Finding all customers");
        return webClient.get()
                .uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(TransactionDto.class)
                .onErrorResume(e -> {
                    log.error("Error finding all customers: {}", e.getMessage());
                    return Flux.error(new RuntimeException("Error finding all customers: " + e.getMessage()));
                });
    }

    /**
     * Obtiene un cliente por su ID.
     * @param id ID del cliente
     * @return Mono de TransactionDto
     */
    public Mono<TransactionDto> findById(String id) {
        log.info("Finding customer by id: {}", id);
        return webClient.get()
                .uri("/customers/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .onErrorResume(e -> {
                    log.error("Error finding customer by id: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error finding customer by id: " + e.getMessage()));
                });
    }

    /**
     * Obtiene un cliente por su número de documento.
     * @param documentNumber Número de documento
     * @return Mono de TransactionDto
     */
    public Mono<TransactionDto> findByDocumentNumber(String documentNumber) {
        log.info("Finding customer by document number: {}", documentNumber);
        return webClient.get()
                .uri("/customers/document/{documentNumber}", documentNumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .onErrorResume(e -> {
                    log.error("Error finding customer by document number: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error finding customer by document number: " + e.getMessage()));
                });
    }

    /**
     * Guarda un nuevo cliente.
     * @param customerDto DTO con los datos del cliente
     * @return Mono de TransactionDto
     */
    public Mono<TransactionDto> save(TransactionDto customerDto) {
        log.info("Saving customer: {}", customerDto);
        return webClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .onErrorResume(e -> {
                    log.error("Error saving customer: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error saving customer: " + e.getMessage()));
                });
    }

    /**
     * Actualiza los datos de un cliente.
     * @param id ID del cliente
     * @param customerDto DTO con los datos a actualizar
     * @return Mono de TransactionDto
     */
    public Mono<TransactionDto> update(String id, TransactionDto customerDto) {
        log.info("Updating customer with id {}: {}", id, customerDto);
        return webClient.put()
                .uri("/customers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .onErrorResume(e -> {
                    log.error("Error updating customer: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error updating customer: " + e.getMessage()));
                });
    }

    /**
     * Elimina un cliente.
     * @param id ID del cliente
     * @return Mono<Void>
     */
    public Mono<Void> delete(String id) {
        log.info("Deleting customer with id: {}", id);
        return webClient.delete()
                .uri("/customers/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    log.error("Error deleting customer: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Error deleting customer: " + e.getMessage()));
                });
    }
}