package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * User Entity for Authentication and Customer Profiles.
 *
 * Architectural Note (Interview Prep):
 * - @Entity marks this class as a persistent JPA entity mapped to table 'users'.
 * - @Table(uniqueConstraints = ...) enforces unique email at the database level.
 * - @Enumerated(EnumType.STRING) stores enum name as VARCHAR instead of ORDINAL integer,
 *   preventing breaking changes if enum constants are reordered.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
