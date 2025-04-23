package com.example.employeeAtt.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employeeAtt.models.Attendance;
import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.models.Institute;
import com.example.employeeAtt.repositories.AttendanceRepository;
import com.example.employeeAtt.repositories.EmployeeRepository;
import com.example.employeeAtt.repositories.InstituteRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private InstituteRepository instituteRepository;

    public Attendance markAttendance(String employeeId, Long instituteId, String remarks) {
        // Step 1: Fetch employee and institute
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        Institute institute = instituteRepository.findById(instituteId)
            .orElseThrow(() -> new RuntimeException("Institute not found"));

        // Step 2: Fetch today's attendance records for the employee
        List<Attendance> todayAttendance = attendanceRepository.findTodayAttendanceByEmployee(employeeId);

        // Step 3: Determine attendance type
        String attendanceType = "Login";
        if (!todayAttendance.isEmpty()) {
            Attendance lastEntry = todayAttendance.get(todayAttendance.size() - 1);
            if ("Login".equalsIgnoreCase(lastEntry.getAttendanceType())) {
                attendanceType = "Logout";
            }
        }

     // Step 4: Validate remarks for Logout attendance
     if ("Logout".equals(attendanceType)) {
        // Check if remarks are empty or null
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new RuntimeException("Remarks are required for Logout attendance.");
        }
    } else {
        // Ensure no remarks for Login
        if (remarks != null && !remarks.trim().isEmpty()) {
            throw new RuntimeException("Remarks should not be provided for Login attendance.");
        }
    }
        
        // Step 5: Create and save new attendance record
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setInstitute(institute);
        attendance.setAttendanceType(attendanceType);
        attendance.setLoginTime(new Date());

        if ("Logout".equals(attendanceType)) {
            attendance.setRemarks(remarks); // Required
        } else {
            attendance.setRemarks(null); // Ignore any remark during login
        }

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
    public List<Attendance> findAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByLoginDate(date);
    }
    
    public List<Employee> getAbsenteesForDate(LocalDate date) {
        // Fetch all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // Get the list of employees who have attended on the given date
        List<Attendance> attendanceRecords = attendanceRepository.findByLoginDate(date);

        // Get the list of employee IDs who attended
        List<String> attendedEmployeeIds = attendanceRecords.stream()
                .map(a -> a.getEmployee().getEmployeeId())
                .collect(Collectors.toList());

        // Filter employees who are not in the attendance list
        List<Employee> absentees = allEmployees.stream()
                .filter(emp -> !attendedEmployeeIds.contains(emp.getEmployeeId()))
                .collect(Collectors.toList());
        return absentees;
    }

        
    // public List<Employee> getTodayAbsentees() {
    //     LocalDate today = LocalDate.now();
    //     List<String> presentEmployeeIds = attendanceRepository.findPresentEmployeeIdsByDate(today);
    //     return employeeRepository.findByEmployeeIdNotIn(presentEmployeeIds);
    // }

   
    // public List<Employee> getAbsenteesByDate(LocalDate date) {
    //     List<String> presentEmployeeIds = attendanceRepository.findPresentEmployeeIdsByDate(date);
    //     return employeeRepository.findByEmployeeIdNotIn(presentEmployeeIds);
    // }

     // Get absentees for a specific date
    public List<Employee> getAbsenteesByDate(LocalDate date) {
        return attendanceRepository.findAbsenteesByDate(date);
    }
    
    // Get absentees for current date
    public List<Employee> getTodayAbsentees() {
        return getAbsenteesByDate(LocalDate.now());
    }

}
