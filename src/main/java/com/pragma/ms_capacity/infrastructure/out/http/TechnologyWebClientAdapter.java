package com.pragma.ms_capacity.infrastructure.out.http;

import com.pragma.ms_capacity.domain.exception.TechnologyNotFoundException;
import com.pragma.ms_capacity.domain.model.Technology;
import com.pragma.ms_capacity.domain.spi.ITechnologyClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TechnologyWebClientAdapter implements ITechnologyClientPort {

    private final WebClient webClient;

    @Override
    public Mono<Technology> findById(Long id) {
        return webClient.get()
                .uri("/api/v1/technologies/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new TechnologyNotFoundException(id)))
                .bodyToMono(Technology.class);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/v1/technologies/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.empty())
                .bodyToMono(Void.class);
    }
}
