package com.example.companyservice.controller;
import com.example.companyservice.dto.CompanyDto;
import com.example.companyservice.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        List<CompanyDto> companies = companyService.getAllCompanies();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long companyId) {
        CompanyDto companyDto = companyService.getCompanyById(companyId);
        return new ResponseEntity<>(companyDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companydto) {
        CompanyDto createdCompany = companyService.createCompany(companydto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long companyId, @RequestBody CompanyDto companyDto) {
        CompanyDto updatedCompany = companyService.updateCompany(companyId, companyDto);
        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);

    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}