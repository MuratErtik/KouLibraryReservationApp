package org.koulibrary.koulibraryreservationapp.services;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.koulibrary.koulibraryreservationapp.exceptions.KeycloakOperationException;
import org.koulibrary.koulibraryreservationapp.exceptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}") private String realm;

    public String createUser(String username, String email,
                             String firstName, String lastName, String password) {

        UserRepresentation user = new UserRepresentation();
        //studentIdNumber
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        // email-verify disabled

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        user.setCredentials(List.of(cred));

        UsersResource users = keycloak.realm(realm).users();
        try (Response response = users.create(user)) {
            if (response.getStatus() == 409) {
                throw new UserAlreadyExistsException("User already exists in Keycloak");
            }
            if (response.getStatus() != 201) {
                throw new KeycloakOperationException("Keycloak user creation failed: " + response.getStatus());
            }
            String keycloakId = CreatedResponseUtil.getCreatedId(response);

            RoleRepresentation userRole = keycloak.realm(realm).roles().get("USER").toRepresentation();
            users.get(keycloakId).roles().realmLevel().add(List.of(userRole));

            return keycloakId;
        }
    }

    public void deleteUser(String keycloakId) {
        keycloak.realm(realm).users().get(keycloakId).remove();
    }

    public void setEnabled(String keycloakId, boolean enabled) {
        UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
        UserRepresentation rep = userResource.toRepresentation();
        rep.setEnabled(enabled);
        userResource.update(rep);
    }
}
