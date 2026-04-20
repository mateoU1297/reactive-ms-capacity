package com.pragma.ms_capacity.infrastructure.config;

import com.pragma.ms_capacity.domain.api.ICapacityServicePort;
import com.pragma.ms_capacity.domain.spi.ICapacityPersistencePort;
import com.pragma.ms_capacity.domain.spi.ITechnologyClientPort;
import com.pragma.ms_capacity.domain.usecase.CapacityUseCase;
import com.pragma.ms_capacity.infrastructure.out.adapter.CapacityPersistenceAdapter;
import com.pragma.ms_capacity.infrastructure.out.http.TechnologyWebClientAdapter;
import com.pragma.ms_capacity.infrastructure.out.mapper.ICapacityEntityMapper;
import com.pragma.ms_capacity.infrastructure.out.repository.CapacityRepository;
import com.pragma.ms_capacity.infrastructure.out.repository.CapacityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final CapacityRepository capacityRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final ICapacityEntityMapper capacityEntityMapper;


    @Bean
    public WebClient technologyWebClient(
            @Value("${clients.technology.url}") String technologyUrl) {
        return WebClient.builder()
                .baseUrl(technologyUrl)
                .build();
    }

    @Bean
    public ITechnologyClientPort technologyClientPort(WebClient technologyWebClient) {
        return new TechnologyWebClientAdapter(technologyWebClient);
    }

    @Bean
    public ICapacityPersistencePort capacityPersistencePort() {
        return new CapacityPersistenceAdapter(capacityRepository, capacityTechnologyRepository, capacityEntityMapper);
    }

    @Bean
    public ICapacityServicePort capacityServicePort(ICapacityPersistencePort capacityPersistencePort, ITechnologyClientPort technologyClientPort) {
        return new CapacityUseCase(capacityPersistencePort, technologyClientPort);
    }
}
