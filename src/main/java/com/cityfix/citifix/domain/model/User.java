package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.valueobject.UserId;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public final class User {
    private final UserId id;
    private final String email;
    private final String password;
    private final Set<String> roles;

    public User(UserId id, String email, String password, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
    }

    public User(Long id, String email, String password, Set<String> roles) {
        this(id != null ? new UserId(id) : null, email, password, roles);
    }

    public static User create(String email, String password, Set<String> roles) {
        return new User((UserId) null, email, password, roles);
    }


    public User updateDetails(String newEmail, Set<String> newRoles) {
        return new User(
                this.id,
                (newEmail != null && !newEmail.isBlank()) ? newEmail : this.email,
                this.password,
                (newRoles != null) ? newRoles : this.roles
        );
    }

    public UserId getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Set<String> getRoles() { return roles; }
}