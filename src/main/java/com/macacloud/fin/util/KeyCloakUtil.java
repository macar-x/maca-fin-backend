package com.macacloud.fin.util;

import com.macacloud.fin.model.auth.UserRegistrationRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;

/**
 * Utility to interact with KeyCloak.
 *
 * @author Emmett
 * @since 2025/02/04
 */
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "auth-server")
public interface KeyCloakUtil {

    @POST
    @Path("/realms/{realm}/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    AccessTokenResponse login(@PathParam("realm") String realm,
                              @FormParam("grant_type") String grantType,
                              @FormParam("username") String username,
                              @FormParam("password") String password,
                              @FormParam("client_id") String clientId,
                              @FormParam("client_secret") String clientSecret);

    @POST
    @Path("/realms/{realm}/protocol/openid-connect/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    AccessTokenResponse refreshToken(@PathParam("realm") String realm,
                                     @FormParam("grant_type") String grantType,
                                     @FormParam("refresh_token") String refreshToken,
                                     @FormParam("client_id") String clientId,
                                     @FormParam("client_secret") String clientSecret);


    @POST
    @Path("/admin/realms/{realm}/users")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(@HeaderParam("Authorization") String authorization,
                        @PathParam("realm") String realm,
                        UserRepresentation userRepresentation);


    default UserRepresentation composeUserRepresentation(UserRegistrationRequest userRegistrationRequest) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRegistrationRequest.getUsername());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setFirstName(userRegistrationRequest.getFirstName());
        user.setLastName(userRegistrationRequest.getLastName());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRegistrationRequest.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        return user;
    }
}
