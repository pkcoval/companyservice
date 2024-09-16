package com.example.companyservice.dto;

import com.example.companyservice.model.Team;
import lombok.Builder;

@Builder
public record TeamDto(Long id, ProjectDto project) {
    public Team toEntity() {
        return Team.builder()
                .id(id)
                .project(project.toEntity())
                .build();
    }
}
