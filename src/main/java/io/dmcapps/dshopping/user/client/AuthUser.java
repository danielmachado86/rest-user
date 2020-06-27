package io.dmcapps.dshopping.user.client;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logging.Logger;

import io.dmcapps.dshopping.user.User;

public class AuthUser {

    private static final Logger LOGGER = Logger.getLogger(
        AuthUser.class);

    public String username;
    public String email;
    public boolean enabled;
    public List<AuthCredential> credentials = new ArrayList<AuthCredential>();

    public AuthUser(User user) {
        this.username = user.email;
        this.email = user.email;
        this.enabled = true;
        this.credentials.add(user.getCredential());
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try {
            jsonResult = mapper
                .writeValueAsString(this);
            
        } catch ( JsonProcessingException  e) {
            LOGGER.error(e.getMessage());
        }
        return jsonResult;
    }
}