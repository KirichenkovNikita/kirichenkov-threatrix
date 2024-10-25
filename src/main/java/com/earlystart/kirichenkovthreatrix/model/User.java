package com.earlystart.kirichenkovthreatrix.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class User {
    @PrimaryKey
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "First name cannot be null")
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    private String organization;
    private String permissions;
}