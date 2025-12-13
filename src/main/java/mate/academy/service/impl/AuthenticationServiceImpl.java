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
        if (password == null || password.isBlank()) {
            throw new AuthenticationException("Password is incorrect, try again");
        }
        if (email == null || !email.matches(EMAIL_FORM)) {
            throw new AuthenticationException("Email is invalid");
        }
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("There is no user with email: "
                        + email
                        + ", you should press 'Register'"));
        String candidatePasswordHash = HashUtil.hashPassword(password, user.getSalt());
        if (!user.getPassword().equals(candidatePasswordHash)) {
            throw new AuthenticationException("Password is incorrect, try again");
        }
        return user;
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
        byte[] salt = HashUtil.getSalt();
        String finalPassword = HashUtil.hashPassword(password, salt);
        User user = new User();
        user.setEmail(email);
        user.setSalt(salt);
        user.setPassword(finalPassword);
        userService.add(user);
        return user;
    }
}
