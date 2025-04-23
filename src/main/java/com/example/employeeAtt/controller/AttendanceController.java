package com.example.employeeAtt.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.employeeAtt.models.Attendance;
import com.example.employeeAtt.repositories.AttendanceRepository;
import com.example.employeeAtt.repositories.EmployeeRepository;
import com.example.employeeAtt.service
.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
     @Autowired
    private AttendanceRepository attendanceRepository;

    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(@RequestBody Map<String, String> request) {
        String employeeId = request.get("employeeId");
        Long instituteId = Long.parseLong(request.get("instituteId"));
        String remarks = request.get("remarks"); // optional
    
        Attendance attendance = attendanceService.markAttendance(employeeId, instituteId, remarks);
        return ResponseEntity.ok(attendance);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalAttendanceCount() {
        long count = attendanceRepository.count(); // Using built-in method to count all records
        return ResponseEntity.ok(count);
    }
    

    // Get today's attendance for all employees


    @GetMapping("/getAll")
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        List<Attendance> attendanceList = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendanceList);
    }
    // Get attendance by specific date
    @GetMapping("/by-date")
    @PreAuthorize("hasRole('ADMIN')") // or remove if you want open access
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        System.out.println("Requested Date: " + date);
        List<Attendance> records = attendanceService.findAttendanceByDate(date);
        return ResponseEntity.ok(records);
    }
}
