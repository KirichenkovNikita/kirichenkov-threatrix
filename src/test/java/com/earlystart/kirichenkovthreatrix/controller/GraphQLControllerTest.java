/*
*    ------ BEGIN LICENSE ATTRIBUTION ------
*    
*    Portions of this file have been appropriated or derived from the following project(s) and therefore require attribution to the original licenses and authors.
*    
*    Repository: https://github.com/spring-projects/spring-boot
*    Source File: spring-boot-project/spring-boot-autoconfigure/src/test/java/org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfigurationIntegrationTests.java
*    Licenses:
*      Apache License 2.0
*      SPDXId: Apache-2.0
*    
*    Auto-attribution by Threatrix, Inc.
*    
*    ------ END LICENSE ATTRIBUTION ------
*/
package com.earlystart.kirichenkovthreatrix.controller;

import com.datastax.oss.driver.api.core.CqlSession;
import com.earlystart.kirichenkovthreatrix.model.User;
import com.earlystart.kirichenkovthreatrix.service.user.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.validation.ValidationException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class GraphQLControllerTest {

    @Autowired
    private UserService userService;

    private User validUser;
    private User invalidUser;

    @Container
    private static final CassandraContainer<?> cassandraContainer =
            new CassandraContainer<>("cassandra:3.11")
                    .withExposedPorts(9042);

    @BeforeAll
    static void startContainer() {
        cassandraContainer.start();
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(cassandraContainer.getContainerIpAddress(), cassandraContainer.getFirstMappedPort()))
                .withLocalDatacenter("datacenter1")
                .build()) {
            session.execute("CREATE KEYSPACE my_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};");
        }

        System.setProperty("spring.cassandra.contact-points", cassandraContainer.getContainerIpAddress());
        System.setProperty("spring.cassandra.port", cassandraContainer.getFirstMappedPort().toString());
    }

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .email("valid@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .organization("MyOrg")
                .permissions("ADMIN")
                .build();

        invalidUser = User.builder()
                .email("invalid-email")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .organization("MyOrg")
                .permissions("ADMIN")
                .build();
    }

    @Test
    void testCreateUser_WithValidData_ShouldSaveUser() {
        var savedUser = userService.createUser(validUser);

        assertNotNull(savedUser);
        assertEquals(validUser.getEmail(), savedUser.getEmail());
        assertEquals(validUser.getFirstName(), savedUser.getFirstName());
    }

    @Test
    void testCreateUser_WithInvalidEmail_ShouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> userService.createUser(invalidUser), "Wrong email");
    }

    @Test
    void testGetAllUsers_WithPaging_ShouldReturnLimitedUserList() {
        userService.createUser(validUser);

        User secondUser = User.builder()
                .email("zsecond@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("password456")
                .organization("MyOrg")
                .permissions("USER")
                .build();
        userService.createUser(secondUser);

        var users = userService.getAllUsers(validUser.getEmail(), 1);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(secondUser.getEmail(), users.get(0).getEmail());

        users = userService.getAllUsers(null, 2);
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testGetUserByEmail_WithValidEmail_ShouldReturnUser() {
        userService.createUser(validUser);

        User foundUser = userService.getUserByEmail(validUser.getEmail());

        assertNotNull(foundUser);
        assertEquals(validUser.getEmail(), foundUser.getEmail());
        assertEquals(validUser.getFirstName(), foundUser.getFirstName());
    }

    @Test
    void testGetUserByEmail_WithInvalidEmail_ShouldReturnNull() {
        User foundUser = userService.getUserByEmail("nonexistent@example.com");

        assertNull(foundUser);
    }

    @Test
    void testGetUsersByOrganization_WithPaging_ShouldReturnLimitedUsers() {
        userService.createUser(validUser);

        User secondUser = User.builder()
                .email("zsecond@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("password456")
                .organization("MyOrg")
                .permissions("USER")
                .build();
        userService.createUser(secondUser);

        var foundUsers = userService.getUsersByOrganization("MyOrg", null, 2);
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());

        foundUsers = userService.getUsersByOrganization("MyOrg", validUser.getEmail(), 1);
        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(secondUser.getEmail(), foundUsers.get(0).getEmail());
    }

    @Test
    void testGetUsersByOrganization_WithInvalidOrganization_ShouldReturnEmpty() {
        var foundUsers = userService.getUsersByOrganization("nonexistentorganization", null, 1);

        assertNotNull(foundUsers);
        assertTrue(foundUsers.isEmpty());
    }

    @Test
    void testCreateOrUpdateUser_WhenUserDoesNotExist_ShouldCreateUser() {
        var newUser = User.builder()
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .password("password123")
                .organization("NewOrg")
                .permissions("USER")
                .build();

        var savedUser = userService.createOrUpdateUser(newUser);

        assertNotNull(savedUser);
        assertEquals(newUser.getEmail(), savedUser.getEmail());
        assertEquals(newUser.getFirstName(), savedUser.getFirstName());
    }

    @Test
    void testCreateOrUpdateUser_WhenUserExists_ShouldUpdateUser() {
        userService.createUser(validUser);

        validUser.setFirstName("UpdatedName");
        validUser.setLastName("UpdatedLastName");
        var updatedUser = userService.createOrUpdateUser(validUser);

        assertNotNull(updatedUser);
        assertEquals("UpdatedName", updatedUser.getFirstName());
        assertEquals("UpdatedLastName", updatedUser.getLastName());
    }

    @Test
    void testDeleteUser_WithValidEmail_ShouldDeleteUser() {
        userService.createUser(validUser);

        userService.deleteUser(validUser.getEmail());
        var deletedUser = userService.getUserByEmail(validUser.getEmail());

        assertNull(deletedUser);
    }

    @Test
    void testDeleteUser_WithInvalidEmail_ShouldNotThrowException() {
        assertDoesNotThrow(() -> userService.deleteUser("nonexistent@example.com"));
    }
}
