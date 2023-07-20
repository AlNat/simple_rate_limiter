package dev.alnat.simpleratelimiter.config;

import dev.alnat.simpleratelimiter.api.Const;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rate limited app")
                        .version("1.0.0")
                        .description("Sample REST API with rate limiting"));
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter authKeyParam = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .schema(new StringSchema())
                    .name(Const.API_KEY_HEADER)
                    .description("Auth key")
                    .required(true);
            operation.addParametersItem(authKeyParam);
            return operation;
        };
    }

}
