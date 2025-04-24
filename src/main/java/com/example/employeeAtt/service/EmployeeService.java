package com.example.employeeAtt.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.models.Role;
import com.example.employeeAtt.repositories.EmployeeRepository;
import com.example.employeeAtt.util.JwtUtil;

@Service
public class EmployeeService {
    
     @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

     @Autowired
    private JavaMailSender mailSender;
     // Register logic
     public String register(Employee employee) {
        if (employee.getRole() == Role.ADMIN) {
            if (employeeRepository.existsByRole(Role.ADMIN)) {
                return "Admin already exists! Because admin registration should be done only once!!!";
            }
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
        return "Registered successfully!";
    }

     // Login logic: using email OR username
     public String loginAndGenerateToken(String emailOrUsername, String password) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);

        if (optionalEmployee.isPresent()) {
            Employee emp = optionalEmployee.get();  
             // Compare hashed password with the provided password
             if (passwordEncoder.matches(password, emp.getPassword())) {
                // Password matches, generate token
                return jwtUtil.generateToken(emp.getUsername());
            }
        }
        return null;
    }
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    /*public String updateEmployeeByEmployeeId(String employeeId, Employee updatedEmployee) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(employeeId);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();

            employee.setFullName(updatedEmployee.getFullName());
            employee.setEmail(updatedEmployee.getEmail());
            employee.setUsername(updatedEmployee.getUsername());
            employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            employee.setPassword(updatedEmployee.getPassword());
            employee.setRole(updatedEmployee.getRole());
            employee.setDesignation(updatedEmployee.getDesignation());
            employee.setWorkLocation(updatedEmployee.getWorkLocation());
            employee.setShiftTimings(updatedEmployee.getShiftTimings());

            employeeRepository.save(employee);
            return "Employee updated successfully.";
        } else {
            return "Employee not found.";
        }
    }

    public String deleteEmployeeByEmployeeId(String employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(employeeId);
        if (optionalEmployee.isPresent()) {
            employeeRepository.delete(optionalEmployee.get());
            return "Employee deleted successfully.";
        } else {
            return "Employee not found.";
        }
    }*/
    public String updateEmployeeByEmployeeId(String employeeId, Employee updatedEmployee) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmployeeId(employeeId); // 🔁 changed from findByEmail
    
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
    
            employee.setFullName(updatedEmployee.getFullName());
            employee.setEmail(updatedEmployee.getEmail());
            employee.setUsername(updatedEmployee.getUsername());
            employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            employee.setPassword(updatedEmployee.getPassword());
            employee.setRole(updatedEmployee.getRole());
            employee.setDesignation(updatedEmployee.getDesignation());
            employee.setWorkLocation(updatedEmployee.getWorkLocation());
            employee.setShiftTimings(updatedEmployee.getShiftTimings());
    
            employeeRepository.save(employee);
            return "Employee updated successfully.";
        } else {
            return "Employee not found.";
        }
    }
    
    public String deleteEmployeeByEmployeeId(String employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmployeeId(employeeId); // 🔁 changed from findByEmail
        if (optionalEmployee.isPresent()) {
            employeeRepository.delete(optionalEmployee.get());
            return "Employee deleted successfully.";
        } else {
            return "Employee not found.";
        }
    }
    //public long countByRoleNot(Role role) {
       // return employeeRepository.countByRoleNot(role);  // Passing Role enum
   // }
   public void forgotPassword(String email) {
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String resetToken = UUID.randomUUID().toString();
        employee.setResetToken(resetToken);
        employeeRepository.save(employee);

        sendResetEmail(email, resetToken);
    }

    private void sendResetEmail(String email, String resetToken) {
        //String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        String resetLink = "https://employeeattendance.vercel.app/reset-password?token=" + resetToken;
        //String resetUrl = "http://your-public-ip:8080/reset-password?token=" + resetToken;

        System.out.println("🔹 Preparing to send reset password email to: " + email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Your Password");
        message.setText("Click the following link to reset your password: " + resetLink);
       //message.setText("Click the following link to reset your password: " + resetUrl);

        mailSender.send(message);
        System.out.println("✅ Email sent successfully to: " + email);
    }

    public String resetPassword(String token, String newPassword) {
        Employee employee = employeeRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setResetToken(null);
        employeeRepository.save(employee);

        return "Password has been reset successfully.";
    }
    

}
