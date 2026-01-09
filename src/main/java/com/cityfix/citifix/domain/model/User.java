package com.cityfix.citifix.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private Set<String> roles;

    public static User create(String email, String encodedPassword, Set<String> roles) {
        return new User(null, email, encodedPassword, roles);
    }
}