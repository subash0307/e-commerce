package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity.
 *
 * Architectural Note (Interview Prep):
 * - Extending JpaRepository provides automatic CRUD, sorting, and pagination.
 * - Method name queries like 'findByEmail' are automatically derived into SQL by Spring Data query parser.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
