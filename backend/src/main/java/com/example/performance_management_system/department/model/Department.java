package com.example.performance_management_system.department.model;

import com.example.performance_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Getter
@Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "head_id")
    private User head;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

