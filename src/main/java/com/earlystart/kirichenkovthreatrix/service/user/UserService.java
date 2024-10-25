package com.earlystart.kirichenkovthreatrix.service.user;


import com.earlystart.kirichenkovthreatrix.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getAllUsers(String startAfterEmail, int limit);

    User getUserByEmail(String email);

    List<User> getUsersByOrganization(String organization, String startAfterEmail, int limit);

    User createOrUpdateUser(User user);

    void deleteUser(String email);
}
