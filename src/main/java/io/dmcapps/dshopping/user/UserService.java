package io.dmcapps.dshopping.user;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional(REQUIRED)
public class UserService {

    @Transactional(SUPPORTS)
    public User findUserById(String id) {
        return User.findById(id);
    }


    public User persistUser(User user) {
        User.persist(user);
        return user;
    }
}
