package com.example.employeeAtt.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.models.Role;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmailOrUsername(String email, String username);
    boolean existsByRole(Role role); // For checking if ADMIN already exists
    Optional<Employee> findByEmployeeId(String employeeId); // ✅ Corrected line
    //long countByRoleNot(String role);
    //long countByRoleNot(Role role);
    Optional<Employee> findByResetToken(String resetToken);
    
    // Find employees by ID list
    List<Employee> findByEmployeeIdNotIn(List<String> employeeIds);


}
