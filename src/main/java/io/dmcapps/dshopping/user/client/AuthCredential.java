package io.dmcapps.dshopping.user.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logging.Logger;

public class AuthCredential {

    private static final Logger LOGGER = Logger.getLogger(
        AuthCredential.class);

    public String type = "password";
    public String value;
    public boolean temporary = false;

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
