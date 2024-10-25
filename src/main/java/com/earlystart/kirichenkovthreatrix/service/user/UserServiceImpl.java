package com.earlystart.kirichenkovthreatrix.service.user;

import com.earlystart.kirichenkovthreatrix.dao.UserDao;
import com.earlystart.kirichenkovthreatrix.model.User;
import com.earlystart.kirichenkovthreatrix.service.validator.Validator;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {
    private static final String DEFAULT_CURSOR_MARK = "-1";
    private final UserDao userDao;
    private final Validator<User> validator;

    @Override
    @GraphQLMutation(name = "createUser", description = "Creates a new user.")
    public User createUser(@Valid @GraphQLArgument(
            name = "user",
            description = "User to be created") User user) {
        validator.validate(user);
        return userDao.save(user);
    }

    @Override
    @GraphQLQuery(name = "getAllUsers", description = "Find all users with optional pagination.")
    public List<User> getAllUsers(
            @GraphQLArgument(name = "startAfterEmail", description = "Email after which to start pagination") String startAfterEmail,
            @GraphQLArgument(name = "limit", description = "Number of users to return") int limit) {
        return userDao.findAllWithPaging(Objects.requireNonNullElse(startAfterEmail, ""), limit);
    }

    @Override
    @GraphQLQuery(name = "getUserByEmail", description = "Find a user by email.")
    public User getUserByEmail(@NotNull @GraphQLArgument(
            name = "email",
            description = "Email of the user") String email) {
        return userDao.findById(email).orElse(null);
    }

    @Override
    @GraphQLQuery(name = "getUsersByOrganization", description = "Find users by organization with optional pagination.")
    public List<User> getUsersByOrganization(
            @NotNull @GraphQLArgument(name = "organization", description = "Organization of the user") String organization,
            @GraphQLArgument(name = "startAfterEmail", description = "Email after which to start pagination") String startAfterEmail,
            @GraphQLArgument(name = "limit", description = "Number of users to return") int limit) {

        return userDao.findByOrganizationWithPagination(
                organization,
                Objects.requireNonNullElse(startAfterEmail, ""),
                limit);
    }

    @Override
    @GraphQLMutation(name = "createOrUpdateUser", description = "Creates or updates a user.")
    public User createOrUpdateUser(@Valid @GraphQLArgument(
            name = "user",
            description = "User to be created or updated") User user) {
        validator.validate(user);
        return userDao.findById(user.getEmail())
                .map(existingUser -> {
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setOrganization(user.getOrganization());
                    existingUser.setPermissions(user.getPermissions());
                    return userDao.save(existingUser);
                })
                .orElseGet(() -> userDao.save(user));
    }

    @Override
    @GraphQLMutation(name = "deleteUser", description = "Deletes a user by Email.")
    public void deleteUser(@GraphQLArgument(name = "email", description = "Email of the user to be deleted") String email) {
        if (userDao.existsByEmail(email)) {
            userDao.deleteByEmail(email);
        }
    }
}
