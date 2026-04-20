package com.pragma.ms_capacity.infrastructure.config;

import com.pragma.ms_capacity.domain.exception.CapacityAlreadyExistsException;
import com.pragma.ms_capacity.domain.exception.DuplicateTechnologyException;
import com.pragma.ms_capacity.domain.exception.InvalidFieldException;
import com.pragma.ms_capacity.domain.exception.InvalidTechnologyCountException;
import com.pragma.ms_capacity.domain.exception.TechnologyNotFoundException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-1)
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorHandler(ErrorAttributes errorAttributes,
                              WebProperties webProperties,
                              ApplicationContext applicationContext,
                              ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::handleError);
    }

    private Mono<ServerResponse> handleError(ServerRequest request) {
        Throwable error = getError(request);
        HttpStatus status;
        String message;

        if (error instanceof CapacityAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
            message = error.getMessage();
        } else if (error instanceof InvalidFieldException ||
                error instanceof InvalidTechnologyCountException ||
                error instanceof DuplicateTechnologyException) {
            status = HttpStatus.BAD_REQUEST;
            message = error.getMessage();
        } else if (error instanceof TechnologyNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = error.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal server error";
        }

        return ServerResponse.status(status)
                .bodyValue(Map.of(
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                ));
    }
}
