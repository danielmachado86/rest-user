package io.dmcapps.dshopping.user;

import java.time.Instant;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

@MongoEntity(collection = "users")
public class User extends PanacheMongoEntityBase {

    private static final Logger LOGGER = Logger.getLogger(
        User.class);
        
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    public ObjectId id;
    public String name;
    public String email;
    public String mobile;
    public ArrayList<ObjectId> addresses;
    public String password_hash;
    public String password_salt;
    public boolean account_verified;
    public Instant date_created;
    public Instant date_verified;
    public Instant date_updated;

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