package team2.race_service.rest;

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
public class TokenController {

    private final RestClient restClient = RestClient.create();

    @Value("${race-service.oauth.token-url}")
    private String tokenUrl;

    @Value("${race-service.oauth.client-id}")
    private String clientId;

    @Value("${race-service.oauth.client-secret}")
    private String clientSecret;

    @Value("${race-service.oauth.redirect-uri}")
    private String redirectUri;

    @GetMapping("/token")
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
