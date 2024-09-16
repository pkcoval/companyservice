package com.example.companyservice;

import com.example.companyservice.dto.*;
import com.example.companyservice.model.Department;
import com.example.companyservice.model.Manager;
import com.example.companyservice.model.Project;
import com.example.companyservice.model.Team;
import com.example.companyservice.repository.DepartmentRepository;
import com.example.companyservice.repository.ManagerRepository;
import com.example.companyservice.repository.ProjectRepository;
import com.example.companyservice.repository.TeamRepository;
import com.example.companyservice.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class CompanyServiceIntegrationTest  {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper; // Umożliwia konwersję obiektów do JSON

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @BeforeEach
    public void cleanDatabase() {
        companyService.getAllCompanies().forEach(companyDto -> companyService.deleteCompany(companyDto.id()));
    }

    @Test
    public void shouldCreateCompany() throws Exception {

        CompanyDto company1 = buildCompanyDto();

        mvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(company1)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(company1.name()));

    }



    @Test
    public void shouldGetCompanies() throws Exception {

        CompanyDto company1 = buildCompanyDto();
        companyService.createCompany(company1);
        companyService.createCompany(company1);

        mvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldGetCompanyById() throws Exception {

        CompanyDto company1 = buildCompanyDto();
        CompanyDto company = companyService.createCompany(company1);

        mvc.perform(get("/companies/{companyId}", company.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(company1.name()));

    }

    @Test
    public void shouldUpdateCompanyDepartmentManagerFields() throws Exception {

        CompanyDto company1 = buildCompanyDto();
        CompanyDto savedCompany = companyService.createCompany(company1);

        Long managerId = getFirstDepartment(savedCompany).teams().get(0).project().manager().id();

        ManagerDto updatedManager = new ManagerDto(managerId, "Updated Contact Info");
        ProjectDto updatedProject = new ProjectDto(getFirstDepartment(savedCompany).teams().get(0).project().id(), updatedManager);
        TeamDto updatedTeam = new TeamDto(getFirstDepartment(savedCompany).teams().get(0).id(), updatedProject);
        DepartmentDto updatedDepartment = new DepartmentDto(getFirstDepartment(savedCompany).id(), "Updated Department", List.of(updatedTeam));
        CompanyDto updatedCompany = new CompanyDto(savedCompany.id(), "Updated Company", List.of(updatedDepartment));


        mvc.perform(put("/companies/{companyId}", savedCompany.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCompany)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Company"));

        CompanyDto companyById = companyService.getCompanyById(savedCompany.id());
        assertThat(companyById.name()).isEqualTo(updatedCompany.name());
        assertThat(getFirstDepartment(companyById).name()).isEqualTo(updatedDepartment.name());
        assertThat(getFirstDepartment(companyById).teams().get(0).project().manager().contactInfo()).isEqualTo(updatedManager.contactInfo());
    }



    @Test
    public void shouldUpdateCompany_DeleteDepartmentAndAddNew() throws Exception {

        CompanyDto company1 = buildCompanyDto();
        CompanyDto savedCompany = companyService.createCompany(company1);

        ManagerDto newManager = new ManagerDto(null, "New Contact Info");
        ProjectDto newProject = new ProjectDto(null, newManager);
        TeamDto newTeam = new TeamDto(null, newProject);
        DepartmentDto newDepartment = new DepartmentDto(null, "New Department", List.of(newTeam));
        // ther is company with only new department and without old department
        CompanyDto updatedCompany = new CompanyDto(savedCompany.id(), "Updated Company", List.of(newDepartment));


        mvc.perform(put("/companies/{companyId}", savedCompany.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCompany)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Company"));

        CompanyDto companyById = companyService.getCompanyById(savedCompany.id());
        assertThat(companyById.name()).isEqualTo(updatedCompany.name());
        assertThat(getFirstDepartment(companyById).name()).isEqualTo(newDepartment.name());
        assertThat(companyById.departments().size()).isEqualTo(1); // it is only one department without old
        assertThat(getFirstDepartment(companyById).id()).isNotEqualTo(getFirstDepartment(savedCompany).id()); // it is new department not update old
        assertThat(getFirstDepartment(companyById).teams().get(0).project().manager().contactInfo()).isEqualTo(newManager.contactInfo());
    }

    @Test
    public void shouldUpdateCompany_DeleteTeamAndAddNew() throws Exception {

        CompanyDto company1 = buildCompanyDto();
        CompanyDto savedCompany = companyService.createCompany(company1);

        ManagerDto newManager = new ManagerDto(null, "New Contact Info");
        ProjectDto newProject = new ProjectDto(null, newManager);
        TeamDto newTeam = new TeamDto(null, newProject);
        DepartmentDto updatedDepartment = new DepartmentDto(getFirstDepartment(savedCompany).id(), "New Department", List.of(newTeam));
        // ther is company with only new department and without old department
        CompanyDto updatedCompany = new CompanyDto(savedCompany.id(), "Updated Company", List.of(updatedDepartment));


        mvc.perform(put("/companies/{companyId}", savedCompany.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCompany)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Company"));

        CompanyDto companyById = companyService.getCompanyById(savedCompany.id());
        assertThat(companyById.departments().get(0).teams().size()).isEqualTo(1); // it is only one team without old
        assertThat(getFirstDepartment(companyById).teams().get(0).id()).isNotEqualTo(updatedDepartment.teams().get(0).id()); // it is new team not update old
    }

    @Test
    public void shouldDeleteCompany() throws Exception {

        CompanyDto company1 = buildCompanyDto();
        CompanyDto company = companyService.createCompany(company1);

        mvc.perform(delete("/companies/{companyId}", company.id()))
                .andExpect(status().isNoContent());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            companyService.getCompanyById(company.id());
        });
        assertThat(exception.getMessage()).isEqualTo("Company with id " + company.id()+ " not found");

        Optional<Department> department = departmentRepository.findById(getFirstDepartment(company).id());
        assertThat(department).isEmpty();

        Optional<Team> team = teamRepository.findById(getFirstDepartment(company).teams().get(0).id());
        assertThat(team).isEmpty();

        Optional<Project> project = projectRepository.findById(getFirstDepartment(company).teams().get(0).project().id());
        assertThat(project).isEmpty();

        Optional<Manager> manager = managerRepository.findById(getFirstDepartment(company).teams().get(0).project().manager().id());
        assertThat(manager).isEmpty();

    }

    private static CompanyDto buildCompanyDto() {
        ManagerDto manager1 = new ManagerDto(null, "Contact info 1");
        ProjectDto project1 = new ProjectDto(null, manager1);

        TeamDto team1 = new TeamDto(null, project1);
        DepartmentDto department1 = new DepartmentDto(null, "Department 1", List.of(team1));
        return new CompanyDto(null, "New Company", List.of(department1));
    }

    private static DepartmentDto getFirstDepartment(CompanyDto savedCompany) {
        return savedCompany.departments().get(0);
    }
}