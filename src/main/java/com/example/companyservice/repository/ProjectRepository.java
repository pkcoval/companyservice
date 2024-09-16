package com.example.companyservice.repository;

import com.example.companyservice.model.Company;
import com.example.companyservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // You can add custom query methods here if needed
}
