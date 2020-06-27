package io.dmcapps.dshopping.user.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthToken {

    public String access_token;
    public String token_type;
    public int expires_in;
    public String refresh_token;
    public int refresh_expires_in;
    public String id_token;
    @JsonProperty("not-before-policy")
    public int not_before_policy;
    public String session_state;
    public String scope;
    
}