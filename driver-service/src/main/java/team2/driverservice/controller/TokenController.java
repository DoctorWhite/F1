package team2.driverservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@Tag(name = "Auth", description = "OAuth2 authorization code → access token exchange")
public class TokenController {

    private final RestClient restClient = RestClient.create();

    @Value("${driver-service.oauth.token-url}")
    private String tokenUrl;

    @Value("${driver-service.oauth.client-id}")
    private String clientId;

    @Value("${driver-service.oauth.client-secret}")
    private String clientSecret;

    @Value("${driver-service.oauth.redirect-uri}")
    private String redirectUri;

    @GetMapping("/token")
    @Operation(summary = "Exchange an OAuth2 authorization code for an access token")
    public String exchangeToken(@RequestParam String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        Map<?, ?> response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .body(body)
                .retrieve()
                .body(Map.class);

        return response != null ? (String) response.get("access_token") : null;
    }
}
