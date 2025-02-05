package com.macacloud.fin.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User Login Inbound Parameters.
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserLoginRequest {

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}
