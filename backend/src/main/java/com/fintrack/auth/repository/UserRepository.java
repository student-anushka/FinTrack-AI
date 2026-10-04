package com.fintrack.auth.repository;

import com.fintrack.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    // existsbyEmail : checks in db whether the email is there or not
    // Spring Data JPA automatically implements it

    Optional<User> findByEmail(String email);

}
    