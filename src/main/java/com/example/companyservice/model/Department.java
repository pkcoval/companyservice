package com.example.companyservice.model;


import com.example.companyservice.dto.CompanyDto;
import com.example.companyservice.dto.DepartmentDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Builder
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany( cascade = {CascadeType.REMOVE, CascadeType.DETACH}, orphanRemoval = true,fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "team_id")
    private List<Team> teams = new ArrayList<>();

    public DepartmentDto toDTO() {
        return DepartmentDto.builder()
                .id(id)
                .name(name)
                .teams(teams.stream().map(Team::toDTO).toList())
                .build();
    }
}