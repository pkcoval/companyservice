package com.example.companyservice.repository;

import com.example.companyservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // You can add custom query methods here if needed
}
