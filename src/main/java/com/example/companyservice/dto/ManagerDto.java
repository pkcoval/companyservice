package com.example.companyservice.dto;

import com.example.companyservice.model.Manager;
import lombok.Builder;

@Builder
public record ManagerDto(Long id, String contactInfo) {

    public Manager toEntity() {
        return Manager.builder()
                .id(id)
                .contactInfo(contactInfo)
                .build();
    }
}
