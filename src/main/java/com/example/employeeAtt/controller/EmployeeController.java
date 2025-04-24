package com.example.employeeAtt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.models.Role;
import com.example.employeeAtt.repositories.EmployeeRepository;
import com.example.employeeAtt.service.EmployeeService;

@CrossOrigin(origins = "https://employeeattendance.vercel.app")
//@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/auth")

public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
     @Autowired
    private EmployeeRepository employeeRepository;

    // Register endpoint
    @PostMapping("/register")
    public String register(@RequestBody Employee employee) {
        return employeeService.register(employee);
    }


    @PostMapping("/login")
    public Object login(@RequestBody Employee loginRequest) {
    String emailOrUsername = loginRequest.getEmail();
    if (emailOrUsername == null || emailOrUsername.isEmpty()) {
        emailOrUsername = loginRequest.getUsername();
    }
    String password = loginRequest.getPassword();

    String token = employeeService.loginAndGenerateToken(emailOrUsername, password);
        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful!");
            return response;
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid email/username or password");
            return errorResponse; // Returning error message in map
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    //@CrossOrigin(origins = "https://employeeattendance.vercel.app")
    //@CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/update/{employeeId}")
    public ResponseEntity<String> updateEmployee(@PathVariable String employeeId, @RequestBody Employee updatedEmployee) {
        String result = employeeService.updateEmployeeByEmployeeId(employeeId, updatedEmployee);
        if (result.equals("Employee updated successfully.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String employeeId) {
        String result = employeeService.deleteEmployeeByEmployeeId(employeeId);
        if (result.equals("Employee deleted successfully.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
 

    @GetMapping("/employees/count")
    public ResponseEntity<Long> getTotalEmployeesCount() {
        long count = employeeRepository.count(); // Using built-in count method
        return ResponseEntity.ok(count);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {
        System.out.println("📥 Received JSON: " + request);
        
        if (request == null || !request.containsKey("email")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "❌ Email is required in request body");
            return ResponseEntity.badRequest().body(error);
        }

        String email = request.get("email");
        employeeService.forgotPassword(email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "✅ Password reset link sent successfully to " + email);

        return ResponseEntity.ok(response);
    }


    //authService.forgotPassword(email);

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        String response = employeeService.resetPassword(token, newPassword);
        return ResponseEntity.ok(response);
        

        }
    


}
