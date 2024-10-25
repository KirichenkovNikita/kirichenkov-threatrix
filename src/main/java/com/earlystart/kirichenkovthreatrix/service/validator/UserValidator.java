package com.earlystart.kirichenkovthreatrix.service.validator;

import com.earlystart.kirichenkovthreatrix.model.User;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;

@Service
public class UserValidator implements Validator<User> {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public void validate(User user) {
        var email = user.getEmail();
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            throw new ValidationException("Wrong email");
        }
    }
}
