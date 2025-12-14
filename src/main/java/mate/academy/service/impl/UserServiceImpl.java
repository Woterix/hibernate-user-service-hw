package mate.academy.service.impl;

import java.util.Optional;
import mate.academy.dao.UserDao;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class UserServiceImpl implements UserService {
    @Inject
    private UserDao userDao;

    @Override
    public User add(User user) {
        byte[] salt = HashUtil.getSalt();
        String hashedPassword = HashUtil.hashPassword(user.getPassword(), salt);
        User userForDb = new User();
        userForDb.setSalt(salt);
        userForDb.setPassword(hashedPassword);
        userForDb.setEmail(user.getEmail());
        userDao.add(userForDb);
        return userForDb;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
