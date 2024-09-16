package com.example.companyservice.repository;

import com.example.companyservice.model.Company;
import com.example.companyservice.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    // You can add custom query methods here if needed
}
