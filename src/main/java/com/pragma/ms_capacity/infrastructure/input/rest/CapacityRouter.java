package com.pragma.ms_capacity.infrastructure.input.rest;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.dto.CapacityResponse;
import com.pragma.ms_capacity.application.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
            ),
            @RouterOperation(
                    path = "/api/v1/capacities",
                    method = RequestMethod.GET,
                    beanClass = CapacityRestHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "findAllCapacities",
                            summary = "List capacities paginated",
                            tags = {"Capacity"},
                            parameters = {
                                    @Parameter(name = "page", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(name = "size", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(name = "sortBy", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "string", allowableValues = {"name", "technologyCount"},
                                                    defaultValue = "name")),
                                    @Parameter(name = "ascending", in = ParameterIn.QUERY,
                                            schema = @Schema(type = "boolean", defaultValue = "true"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200",
                                            content = @Content(schema = @Schema(implementation = PagedResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid parameters")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/capacities/{id}",
                    method = RequestMethod.GET,
                    beanClass = CapacityRestHandler.class,
                    beanMethod = "findById",
                    operation = @Operation(
                            operationId = "findCapacityById",
                            summary = "Find capacity by id",
                            tags = {"Capacity"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200",
                                            content = @Content(
                                                    schema = @Schema(implementation = CapacityResponse.class)
                                            )),
                                    @ApiResponse(responseCode = "404", description = "Capacity not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/capacities/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = CapacityRestHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteCapacity",
                            summary = "Delete a capacity",
                            tags = {"Capacity"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Capacity deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Capacity not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> capacityRoutes(CapacityRestHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/capacities", handler::save)
                .GET("/api/v1/capacities/{id}", handler::findById)
                .GET("/api/v1/capacities", handler::findAll)
                .DELETE("/api/v1/capacities/{id}", handler::delete)
                .build();
    }
}
