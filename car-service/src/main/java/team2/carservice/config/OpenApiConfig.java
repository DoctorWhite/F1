package team2.carservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String OAUTH_SCHEME = "MUNI";
    private static final String BEARER_SCHEME = "BearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Car Service API")
                        .version("1.0")
                        .description("API for managing F1 cars and their assembly."));
    }

    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> {
            openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
            openApi.getComponents()
                    .addSecuritySchemes(OAUTH_SCHEME,
                            new SecurityScheme()
                                    .type(SecurityScheme.Type.OAUTH2)
                                    .description("Get access token with OAuth 2 Authorization Code Grant")
                                    .flows(new OAuthFlows()
                                            .authorizationCode(new OAuthFlow()
                                                    .authorizationUrl("https://id.muni.cz/oidc/authorize")
                                                    .tokenUrl("https://id.muni.cz/oidc/token")
                                                    .scopes(new Scopes()
                                                            .addString("openid", "get user identifier")
                                                            .addString("test_read", "read access to car data")
                                                            .addString("test_write", "write access to car data")
                                                    )
                                            )
                                    )
                    )
                    .addSecuritySchemes(BEARER_SCHEME,
                            new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                                    .description("Paste the raw MUNI access token only. Do not include the 'Bearer ' prefix.")
                    );
        };
    }
}
