package com.pragma.ms_capacity.infrastructure.input.rest;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.dto.CapacityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CapacityRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/capacities",
                    method = RequestMethod.POST,
                    beanClass = CapacityRestHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "saveCapacity",
                            summary = "Register a new capacity",
                            tags = {"Capacity"},
                            parameters = {},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = CapacityRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Capacity created successfully",
                                            content = @Content(schema = @Schema(implementation = CapacityResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid field"),
                                    @ApiResponse(responseCode = "404", description = "Technology not found"),
                                    @ApiResponse(responseCode = "409", description = "Capacity already exists")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> capacityRoutes(CapacityRestHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/capacities", handler::save)
                .build();
    }
}
