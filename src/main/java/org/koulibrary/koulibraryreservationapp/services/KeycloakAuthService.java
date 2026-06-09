package org.koulibrary.koulibraryreservationapp.services;

import org.koulibrary.koulibraryreservationapp.dtos.responses.TokenResponse;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service

public class KeycloakAuthService {

    private final RestClient restClient = RestClient.create();
    private final String tokenUri;
    private final String logoutUri;
    private final String clientId;
    private final String clientSecret;

    public KeycloakAuthService(
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.realm}")           String realm,
            @Value("${keycloak.client-id}")       String clientId,
            @Value("${keycloak.client-secret}")   String clientSecret) {
        String base = authServerUrl + "/realms/" + realm + "/protocol/openid-connect";
        this.tokenUri  = base + "/token";
        this.logoutUri = base + "/logout";
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public TokenResponse login(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", username);
        form.add("password", password);
        form.add("scope", "openid");
        return postToken(form);
    }

    public TokenResponse refresh(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        return postToken(form);
    }

    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);

        restClient.post()
                .uri(logoutUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity();
    }

    private TokenResponse postToken(MultiValueMap<String, String> form) {
        return restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InvalidCredentialsException("Authentication failed");
                })
                .body(TokenResponse.class);
    }
}
