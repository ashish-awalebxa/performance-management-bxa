package com.example.performance_management_system.user.repository;

import com.example.performance_management_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByManagerId(Long managerId);
}
