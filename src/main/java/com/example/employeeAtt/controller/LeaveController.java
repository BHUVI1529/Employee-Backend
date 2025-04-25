package com.example.employeeAtt.controller;

import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.models.Leave;
import com.example.employeeAtt.service.EmployeeService;
import com.example.employeeAtt.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    // Employee applies for leave
     @PreAuthorize("hasAnyRole('TEACHING', 'NON_TEACHING')")
     @PostMapping("/apply")
     public ResponseEntity<String> applyLeave(@RequestParam String employeeId,
                                              @RequestBody Leave leave) {
         Employee employee = employeeService.getEmployeeById(employeeId);
         if (employee == null) {
             return ResponseEntity.badRequest().body("Employee not found");
         }
     
         leave.setEmployee(employee);
         leave.setStatus("Pending");
         leaveService.saveLeave(leave);
         return ResponseEntity.ok("Leave request submitted successfully.");
     }
     
    // Admin updates leave status (approve or reject)
    @PutMapping("/{leaveId}/status")
    public ResponseEntity<String> updateLeaveStatus(@PathVariable Long leaveId,
                                                    @RequestParam String status,
                                                    @RequestParam String employeeId) {
        try {
            // Update the leave status (Approved/Rejected)
            leaveService.updateLeaveStatus(employeeId, leaveId, status);

            return ResponseEntity.ok("Leave status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update leave status: " + e.getMessage());
        }
    }

    // Get total on-leave employees
    @GetMapping("/on-leave-count")
    public ResponseEntity<Long> getOnLeaveCount() {
        long count = leaveService.countOnLeave();
        return ResponseEntity.ok(count);
    }

    // Get all leave requests (Admin can view all requests)
    @GetMapping("/all")
    public ResponseEntity<List<Leave>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveService.getAllLeaveRequests());
    }
}