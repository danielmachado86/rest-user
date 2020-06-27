package io.dmcapps.dshopping.user;

import java.time.Instant;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import io.dmcapps.dshopping.user.client.AuthCredential;
import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;

@MongoEntity(collection = "users")
public class User extends PanacheMongoEntityBase {
        
    @BsonId
    public String id;
    public String name;
    public String email;
    public String mobile;
    @BsonIgnore
    private AuthCredential credential; 
    public ArrayList<ObjectId> addresses;
    public Instant date_created;
    public Instant date_updated;
    
	public AuthCredential getCredential() {
        return credential;
    }
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public void setCredential(AuthCredential credential) {
		this.credential = credential;
	}

}