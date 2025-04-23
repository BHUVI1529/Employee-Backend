package com.example.employeeAtt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employeeAtt.models.Institute;

public interface InstituteRepository extends JpaRepository<Institute, Long>{
    
    Optional<Institute> findByInstituteName(String instituteName);
}
