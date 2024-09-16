package com.example.companyservice.model;

import com.example.companyservice.dto.CompanyDto;
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
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.DETACH}, orphanRemoval = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "department_id")
    private List<Department> departments = new ArrayList<>();

    public CompanyDto toDTO() {
        return CompanyDto.builder()
                .id(id)
                .name(name)
                .departments(departments.stream().map(Department::toDTO).toList())
                .build();
    }
}
