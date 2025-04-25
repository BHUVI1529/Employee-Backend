package com.example.employeeAtt.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employeeAtt.models.Institute;
import com.example.employeeAtt.repositories.InstituteRepository;

@Service
public class InstituteService {
    @Autowired
    private InstituteRepository instituteRepository;

    public Optional<Institute> getInstituteByName(String instituteName) {
        return instituteRepository.findByInstituteName(instituteName);
    }
}
