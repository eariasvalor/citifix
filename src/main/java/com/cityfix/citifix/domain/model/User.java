package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.valueobject.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class User {
    private UserId id;
    private String email;
    private String password;
    private Set<String> roles;

    public User(Long id, String email, String password, Set<String> roles) {
        this.id = (id != null) ? new UserId(id) : null;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(UserId id, String email, String password, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public static User create(String email, String password, Set<String> roles) {
        return new User((UserId) null, email, password, roles);
    }

    public UserId getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Set<String> getRoles() { return roles; }
}