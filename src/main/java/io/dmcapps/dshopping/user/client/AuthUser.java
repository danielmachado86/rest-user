package io.dmcapps.dshopping.user.client;

import java.util.ArrayList;
import java.util.List;

import io.dmcapps.dshopping.user.User;

public class AuthUser {

    public String username;
    public String email;
    public boolean enabled;
    public List<AuthCredential> credentials = new ArrayList<AuthCredential>();

    public AuthUser(User user) {
        this.username = user.email;
        this.email = user.email;
        this.enabled = true;
        this.credentials.add(user.credential);
    }

}