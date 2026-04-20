package com.pragma.ms_capacity.domain.usecase;

import com.pragma.ms_capacity.domain.exception.CapacityAlreadyExistsException;
import com.pragma.ms_capacity.domain.exception.DuplicateTechnologyException;
import com.pragma.ms_capacity.domain.exception.InvalidTechnologyCountException;
import com.pragma.ms_capacity.domain.exception.TechnologyNotFoundException;
import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.Technology;
import com.pragma.ms_capacity.domain.spi.ICapacityPersistencePort;
import com.pragma.ms_capacity.domain.spi.ITechnologyClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityUseCaseTest {

    @Mock
    private ICapacityPersistencePort capacityPersistencePort;
    @Mock private ITechnologyClientPort technologyClientPort;

    @InjectMocks
    private CapacityUseCase capacityUseCase;

    private Capacity capacity;
    private List<Technology> technologies;

    @BeforeEach
    void setUp() {
        technologies = List.of(
                new Technology(1L, "Java"),
                new Technology(2L, "Spring"),
                new Technology(3L, "Docker")
        );
        capacity = new Capacity(null, "Backend", "Backend capacity", technologies);
    }

    @Test
    void save_validCapacity_success() {
        when(capacityPersistencePort.existsByName("Backend")).thenReturn(Mono.just(false));
        when(technologyClientPort.findById(1L)).thenReturn(Mono.just(technologies.get(0)));
        when(technologyClientPort.findById(2L)).thenReturn(Mono.just(technologies.get(1)));
        when(technologyClientPort.findById(3L)).thenReturn(Mono.just(technologies.get(2)));
        when(capacityPersistencePort.save(any())).thenReturn(Mono.just(capacity));

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectNextMatches(c -> c.getName().equals("Backend"))
                .verifyComplete();
    }

    @Test
    void save_nameExists_throwsAlreadyExists() {
        when(capacityPersistencePort.existsByName("Backend")).thenReturn(Mono.just(true));

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectError(CapacityAlreadyExistsException.class)
                .verify();
    }

    @Test
    void save_technologyNotFound_throwsNotFound() {
        when(capacityPersistencePort.existsByName("Backend")).thenReturn(Mono.just(false));
        when(technologyClientPort.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectError(TechnologyNotFoundException.class)
                .verify();
    }

    @Test
    void save_duplicateTechnologies_throwsDuplicate() {
        capacity.setTechnologies(List.of(
                new Technology(1L, "Java"),
                new Technology(1L, "Java"),
                new Technology(2L, "Spring")
        ));

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectError(DuplicateTechnologyException.class)
                .verify();
    }

    @Test
    void save_lessThanMinTechnologies_throwsInvalidCount() {
        capacity.setTechnologies(List.of(
                new Technology(1L, "Java"),
                new Technology(2L, "Spring")
        ));

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectError(InvalidTechnologyCountException.class)
                .verify();
    }

    @Test
    void save_moreThanMaxTechnologies_throwsInvalidCount() {
        List<Technology> tooMany = new ArrayList<>();
        for (long i = 1; i <= 21; i++)
            tooMany.add(new Technology(i, "Tech" + i));
        capacity.setTechnologies(tooMany);

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectError(InvalidTechnologyCountException.class)
                .verify();
    }

    @Test
    void save_exactlyMinTechnologies_success() {
        when(capacityPersistencePort.existsByName("Backend")).thenReturn(Mono.just(false));
        when(technologyClientPort.findById(any())).thenReturn(Mono.just(technologies.get(0)));
        when(capacityPersistencePort.save(any())).thenReturn(Mono.just(capacity));

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save_exactlyMaxTechnologies_success() {
        List<Technology> maxTechs = new ArrayList<>();
        for (long i = 1; i <= 20; i++)
            maxTechs.add(new Technology(i, "Tech" + i));
        capacity.setTechnologies(maxTechs);

        when(capacityPersistencePort.existsByName("Backend")).thenReturn(Mono.just(false));
        when(technologyClientPort.findById(any())).thenReturn(Mono.just(technologies.get(0)));
        when(capacityPersistencePort.save(any())).thenReturn(Mono.just(capacity));

        StepVerifier.create(capacityUseCase.save(capacity))
                .expectNextCount(1)
                .verifyComplete();
    }
}