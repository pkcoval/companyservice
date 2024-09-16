package com.example.companyservice.model;

import com.example.companyservice.dto.DepartmentDto;
import com.example.companyservice.dto.TeamDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.stream.Collectors;
@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.DETACH}, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    public TeamDto toDTO() {
        return TeamDto.builder()
                .id(id)
                .project(project.toDTO())
                .build();
    }
}
