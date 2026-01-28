package com.example.performance_management_system.review.repository;


import com.example.performance_management_system.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
