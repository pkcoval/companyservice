package com.example.companyservice.model;

import com.example.companyservice.dto.ManagerDto;
import com.example.companyservice.dto.ProjectDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "managers")
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contactInfo;

    public ManagerDto toDTO() {
        return ManagerDto.builder()
                .id(id)
                .contactInfo(contactInfo)
                .build();
    }
}