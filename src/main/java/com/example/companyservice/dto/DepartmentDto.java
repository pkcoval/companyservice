package com.example.companyservice.dto;

import com.example.companyservice.model.Department;
import lombok.Builder;

import java.util.List;

@Builder
public record DepartmentDto(Long id, String name, List<TeamDto> teams) {

    public Department toEntity() {
        return Department.builder()
                .id(id)
                .name(name)
                .teams(teams.stream().map(TeamDto::toEntity).toList())
                .build();
    }
}
