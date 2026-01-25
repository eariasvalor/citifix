package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity;

import com.cityfix.citifix.domain.model.valueobject.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    public static UserJpaEntity fromDomain(com.cityfix.citifix.domain.model.User user) {
        return new UserJpaEntity(
                (user.getId() != null) ? user.getId().value() : null,
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
    }

    public com.cityfix.citifix.domain.model.User toDomain() {
        return new com.cityfix.citifix.domain.model.User(
                new com.cityfix.citifix.domain.model.valueobject.UserId(this.id),
                this.email,
                this.password,
                this.roles != null ? new java.util.HashSet<>(this.roles) : new java.util.HashSet<>()
        );
    }
}