package com.example.companyservice.repository;

import com.example.companyservice.model.Company;
import com.example.companyservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // You can add custom query methods here if needed
}
