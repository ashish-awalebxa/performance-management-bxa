package com.example.performance_management_system.review.repository;


import com.example.performance_management_system.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<Review> findByManagerId(Long managerId, Pageable pageable);
}
