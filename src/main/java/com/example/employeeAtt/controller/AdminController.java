package com.example.employeeAtt.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeeAtt.models.Attendance;
import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.service.AttendanceService;
import com.example.employeeAtt.service.EmployeeService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/absentees")
public ResponseEntity<List<Employee>> getAbsentees(@RequestBody Map<String, String> request) {
    String dateString = request.get("date");
    LocalDate date = LocalDate.parse(dateString);
    List<Employee> absentees = attendanceService.getAbsenteesForDate(date);
    return ResponseEntity.ok(absentees);
}
    // Endpoint to mark absentee data (if required for any reason, although this can be inferred from attendance absence)
    @PostMapping("/mark-absent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> markAbsent(@RequestBody Map<String, String> request) {
        String employeeId = request.get("employeeId");
        LocalDate date = LocalDate.parse(request.get("date"));
        
        // Check if the employee has attendance on the given date
        List<Attendance> attendance = attendanceService.findAttendanceByDate(date);

        // Check if the employee has any attendance for the date
        boolean isAbsent = attendance.stream()
            .noneMatch(a -> a.getEmployee().getEmployeeId().equals(employeeId));

        if (isAbsent) {
            // Logic for handling absentee marking (optional, maybe notify or store separately)
            return ResponseEntity.ok("Employee " + employeeId + " is marked as absent on " + date);
        } else {
            return ResponseEntity.badRequest().body("Employee has already marked attendance on this date.");
        }
    }
    // Endpoint to get absentee data by date
    
    // Current day absentees
    @GetMapping("/absentees/today")
    public List<Employee> getTodayAbsentees() {
        return attendanceService.getTodayAbsentees();
    }

    // Specific date absentees
    @GetMapping("/absentees")
    public List<Employee> getAbsenteesByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAbsenteesByDate(date);
    }
}