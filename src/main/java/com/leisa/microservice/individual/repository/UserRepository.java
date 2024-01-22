package com.leisa.microservice.individual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.leisa.microservice.individual.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // You can add custom query methods here if needed

    boolean existsByUsernameOrEmail(String username, String email);
    // Check if there are any users with the same username or email, excluding the given user ID
    @Query("SELECT COUNT(u) FROM User u WHERE (u.username = :username OR u.email = :email) AND u.id <> :userId")
    int countUsersWithSameUsernameOrEmail(@Param("username") String username, @Param("email") String email, @Param("userId") Long userId);

    Optional<User> findByUsername(String username);
    //User findByUsername(String username);

    // // Add a custom query method to find a queue by publisher ID and event type
    // List<QueueMapping> findByPublisherIdAndEventType(Long publisherId, String eventType);

}


