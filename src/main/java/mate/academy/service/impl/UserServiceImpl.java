package mate.academy.service.impl;

import java.util.Optional;
import mate.academy.dao.UserDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Inject
    private UserDao userDao;

    @Override
    public User add(User user) {
        try {
            return userDao.add(user);
        } catch (DataProcessingException e) {
            System.out.println("Can't add user to db" + user);
            return user;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return userDao.findByEmail(email);
        } catch (DataProcessingException e) {
            System.out.println("User not found in DB: " + email);
            return Optional.empty();
        }
    }
}
