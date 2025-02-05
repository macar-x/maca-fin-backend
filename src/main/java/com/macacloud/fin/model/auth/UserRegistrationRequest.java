package com.macacloud.fin.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User Save Inbound Parameters.
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserRegistrationRequest {

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "mobile_phone")
    private String mobilePhone;
}
