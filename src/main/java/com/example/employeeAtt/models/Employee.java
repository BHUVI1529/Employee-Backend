package com.example.employeeAtt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    private String employeeId;  // Primary key, e.g., EMP001

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    private String phoneNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, TEACHING, NON_TEACHING

    private String designation;

    private String workLocation;

    private String shiftTimings;

    @Column(unique = true) // Reset token should be unique
    private String resetToken;
}
