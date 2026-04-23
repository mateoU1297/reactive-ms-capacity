package com.pragma.ms_capacity.infrastructure.input.rest;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.handler.ICapacityHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CapacityRestHandler {

    private final ICapacityHandler capacityHandler;

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CapacityRequest.class)
                .flatMap(capacityHandler::save)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        boolean ascending = Boolean.parseBoolean(request.queryParam("ascending").orElse("true"));

        return capacityHandler.findAll(page, size, sortBy, ascending)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}
