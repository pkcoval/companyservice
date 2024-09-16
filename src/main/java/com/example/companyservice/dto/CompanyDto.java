package com.example.companyservice.dto;

import com.example.companyservice.model.Company;
import lombok.Builder;

import java.util.List;

@Builder
public record CompanyDto(Long id, String name, List<DepartmentDto> departments) {

    public Company toEntity() {
        return Company.builder()
                .id(id)
                .name(name)
                .departments(departments.stream().map(DepartmentDto::toEntity).toList())
                .build();
    }
}

