package dev.alnat.simpleratelimiter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/client")
@Tag(name = "REST API for client operation")
public class ClientAPI {

    @Operation(summary = "Get data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request complete"),
            @ApiResponse(responseCode = "403", description = "No auth key"),
            @ApiResponse(responseCode = "419", description = "Too much request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping(value =  "/data/{id}")
    public Integer find(@Parameter(in = ParameterIn.PATH, description = "ID", required = true)
                        @PathVariable @Positive Integer id) {
        return id;
    }

}
