package com.example.employeeAtt.service;

import com.example.employeeAtt.models.Employee;
import com.example.employeeAtt.models.Leave;
import com.example.employeeAtt.repositories.EmployeeRepository;
import com.example.employeeAtt.repositories.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Leave saveLeave(Leave leave) {
        return leaveRepository.save(leave);
    }

    public List<Leave> getAllLeaveRequests() {
        return leaveRepository.findAll();
    }

    public void updateLeaveStatus(String employeeId, Long leaveId, String status) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        if (!leave.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized update");
        }
        leave.setStatus(status);
        leaveRepository.save(leave);
    }

    public long countOnLeave() {
        return leaveRepository.findByStatus("Accepted").size();
    }
}