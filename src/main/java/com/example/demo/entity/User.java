package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(nullable = false)
    private String role;

}
