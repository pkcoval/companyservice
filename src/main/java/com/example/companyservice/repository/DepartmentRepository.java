package com.example.companyservice.repository;

import com.example.companyservice.model.Company;
import com.example.companyservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // You can add custom query methods here if needed
}
