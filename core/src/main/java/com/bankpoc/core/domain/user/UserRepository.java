package com.bankpoc.core.domain.user;


import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.accounts a " + // JOIN FETCH the Account entity
            "LEFT JOIN FETCH a.card c " +    // JOIN FETCH the Card entity through the Account
            "WHERE u.email = :email")
    Optional<User> findUserWithAccountAndCardByEmail(@Param("email") String email);
}
