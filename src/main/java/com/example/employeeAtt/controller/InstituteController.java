package com.example.employeeAtt.controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.employeeAtt.models.Institute;
import com.example.employeeAtt.service.InstituteService;

@RestController
@RequestMapping("/api/institute")

//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://employeeattendance.vercel.app")

public class InstituteController {
    
    @Autowired
    private InstituteService instituteService;

    @GetMapping("/id")
    public ResponseEntity<?> getInstituteIdByName(@RequestParam String instituteName) {
        Optional<Institute> institute = instituteService.getInstituteByName(instituteName);

        if (institute.isPresent()) {
            return ResponseEntity.ok(institute.get().getId());
        } else {
            return ResponseEntity.status(404).body("Institute not found with name: " + instituteName);
        }
    }
}
