package com.macacloud.fin.model.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User Token Response.
 *
 * @author Emmett
 * @since 2025/02/04
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TokenResponse {

    public String access_token;
    public String refresh_token;
    public String expires_in;
    public String refresh_expires_in;
    public String token_type;
    public String not_before_policy;
    public String session_state;
    public String scope;
}
