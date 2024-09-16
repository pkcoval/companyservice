package com.example.companyservice.model;

import com.example.companyservice.dto.ProjectDto;
import com.example.companyservice.dto.TeamDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.DETACH}, orphanRemoval = true)
    @JoinColumn(name = "manager_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Manager manager;

    public ProjectDto toDTO() {
        return ProjectDto.builder()
                .id(id)
                .manager(manager.toDTO())
                .build();
    }

}
