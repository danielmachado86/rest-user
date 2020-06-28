package io.dmcapps.dshopping.user;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

@ApplicationScoped
@Transactional(REQUIRED)
public class UserService {

    @Transactional(SUPPORTS)
    public User findUserById(String id) {
        return User.findById(id);
    }

    @Transactional(SUPPORTS)
    public List<User> findAllUsers() {
        return User.listAll();
    }

    @Transactional(SUPPORTS)
    public User findUserById(Long id) {
        return User.findById(id);
    }

    public User updateUser(@Valid User user) {
        User entity = User.findById(user.id);
        entity.name = user.name;
        entity.email = user.email;
        entity.addresses = user.addresses;
        entity.mobile = user.mobile;
        return entity;
    }

    public User persistUser(User user) {
        User.persist(user);
        return user;
    }

    public void deleteUser(Long id) {
        User user = User.findById(id);
        user.delete();
    }
}
