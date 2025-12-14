package mate.academy.service.impl;

import java.util.Optional;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String EMAIL_FORM = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.]+$";
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> byEmail = userService.findByEmail(email);
        if (email == null || !email.matches(EMAIL_FORM)
                || byEmail.isEmpty() || !byEmail.get().getPassword()
                .equals(HashUtil.hashPassword(password, byEmail.get().getSalt()))) {
            throw new AuthenticationException("Can't login, email or password is incorrect");
        }
        return byEmail.get();
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (password == null || password.isBlank()) {
            throw new RegistrationException("Password is not allowed");
        }
        if (email == null || !email.matches(EMAIL_FORM)) {
            throw new RegistrationException("Email is invalid");
        }
        Optional<User> byEmail = userService.findByEmail(email);
        if (byEmail.isPresent()) {
            throw new RegistrationException("User with email: " + email
                    + " is already registered");
        }
        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        return userService.add(user);
    }
}
