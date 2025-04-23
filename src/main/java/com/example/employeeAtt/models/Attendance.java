package com.example.employeeAtt.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key for Attendance Table

    // Foreign key to Employee table
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId", nullable = false)
    private Employee employee;

    // Foreign key to Institute table
    @ManyToOne
    @JoinColumn(name = "institute_id", referencedColumnName = "id", nullable = false)
    private Institute institute;

    // This stores both login or logout time, depending on attendanceType
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "attendance_time", nullable = false)
    private Date loginTime;

    // Either "Login" or "Logout"
    @Column(name = "attendance_type", nullable = false)
    private String attendanceType;

     // Remarks are used only for logout entries
     @Column(length = 500)
     private String remarks;



}
