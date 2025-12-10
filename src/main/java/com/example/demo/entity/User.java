package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * JPA entity representing an application user.
 *
 * Main concept:
 * - Maps the `User` Java object to the `users` database table.
 * - Holds persistent fields: id, name, email, password, role.
 * - Uses Lombok to provide boilerplate (getters/setters, builders, constructors).
 *
 * Responsibilities:
 * - Be stored/retrieved by JPA repositories (e.g. `UserRepository`).
 * - Contain only persistence-level data; avoid business logic here.
 */
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    public static enum Role {
        USER,
        ADMIN
    }

    @ManyToMany
    @JoinTable(name = "wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JsonIgnore
    @Builder.Default
    private Set<com.example.demo.entity.Product> wishlist = new HashSet<>();

        @jakarta.persistence.OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        @Builder.Default
        private java.util.List<com.example.demo.entity.CartItem> cart = new java.util.ArrayList<>();

}
