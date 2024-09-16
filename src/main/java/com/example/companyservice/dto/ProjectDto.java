package com.example.companyservice.dto;

import com.example.companyservice.model.Project;
import lombok.Builder;

@Builder
public record ProjectDto(Long id, ManagerDto manager) {

    public Project toEntity() {
        return Project.builder()
                .id(id)
                .manager(manager.toEntity())
                .build();
    }
}
