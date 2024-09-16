package com.example.companyservice.service;

import com.example.companyservice.dto.CompanyDto;
import com.example.companyservice.dto.DepartmentDto;
import com.example.companyservice.model.*;
import com.example.companyservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final DepartmentRepository departmentRepository;

    private final TeamRepository teamRepository;

    private final ProjectRepository projectRepository;

    private final ManagerRepository managerRepository;

    public CompanyService(CompanyRepository companyRepository, DepartmentRepository departmentRepository, TeamRepository teamRepository, ProjectRepository projectRepository, ManagerRepository managerRepository) {
        this.companyRepository = companyRepository;
        this.departmentRepository = departmentRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.managerRepository = managerRepository;
    }

    @Transactional
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream().map(Company::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CompanyDto getCompanyById(Long companyId) {
        Optional<Company> company = companyRepository.findById(companyId);
        return company
                .map(Company::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Company with id " + companyId + " not found"));
    }

    @Transactional
    public CompanyDto createCompany(CompanyDto companyDto) {
        List<Department> departmentsToSaved = new ArrayList<>();
        Company company = companyDto.toEntity();
        for (Department department : company.getDepartments()) {
            List<Team> teamsToSaved = new ArrayList<>();
            for (Team team : department.getTeams()) {
                Project project = team.getProject();

                Manager savedManager = managerRepository.save(project.getManager());
                project.setManager(savedManager);

                Project savedProject = projectRepository.save(project);
                team.setProject(savedProject);

                Team savedTeam = teamRepository.save(team);
                teamsToSaved.add(savedTeam);
            }
            department.setTeams(teamsToSaved);
            Department savedDepartment = departmentRepository.save(department);
            departmentsToSaved.add(savedDepartment);
        }
        company.setDepartments(departmentsToSaved);
        return companyRepository.save(company).toDTO();
    }



    @Transactional
    public CompanyDto updateCompany(Long companyId, CompanyDto updatedCompany) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with id " + companyId + " not found"));

        company.setName(updatedCompany.name());
        updateDepartments(company, updatedCompany.departments().stream()
                .map(DepartmentDto::toEntity)
                .toList());

        return companyRepository.save(company).toDTO();
    }

    @Transactional
    public void updateDepartments(Company databaseCompany, List<Department> updatedDepartments) {
        List<Long> newIds = updatedDepartments.stream()
                .map(Department::getId)
                .toList();

        removeOldDepartments(databaseCompany, newIds);

        for (Department updatedDepartment : updatedDepartments) {
            updateOrAddDepartment(databaseCompany, updatedDepartment);
        }
    }

    @Transactional
    public void removeOldDepartments(Company databaseCompany, List<Long> newIds) {
        databaseCompany.getDepartments().removeIf(department -> !newIds.contains(department.getId()));
    }

    @Transactional
    public void updateOrAddDepartment(Company databaseCompany, Department updatedDepartment) {
        Optional<Department> existingDepartmentOpt = databaseCompany.getDepartments().stream()
                .filter(department -> department.getId().equals(updatedDepartment.getId()))
                .findFirst();

        if (existingDepartmentOpt.isPresent()) {
            updateDepartment(existingDepartmentOpt.get(), updatedDepartment);
        } else {
            addNewDepartment(databaseCompany, updatedDepartment);
        }
    }

    @Transactional
    public void updateDepartment(Department databaseDepartment, Department updatedDepartment) {
        updateTeams(databaseDepartment, updatedDepartment.getTeams());
        databaseDepartment.setName(updatedDepartment.getName());
        departmentRepository.save(databaseDepartment);
    }

    @Transactional
    public void addNewDepartment(Company databaseCompany, Department updatedDepartment) {
        updateTeams(null, updatedDepartment.getTeams());
        Department savedDepartment = departmentRepository.save(updatedDepartment);
        databaseCompany.getDepartments().add(savedDepartment);
    }

    @Transactional
    public void updateTeams(Department databaseDepartment, List<Team> updatedTeams) {
        if (databaseDepartment != null) {
            List<Long> newIds = updatedTeams.stream()
                    .map(Team::getId)
                    .toList();

            removeOldTeams(databaseDepartment, newIds);

            for (Team updatedTeam : updatedTeams) {
                updateOrAddTeam(databaseDepartment, updatedTeam);
            }
        } else {
            for (Team updatedTeam : updatedTeams) {
                updateProject(null, updatedTeam.getProject());
                teamRepository.save(updatedTeam);
            }
        }
    }

    @Transactional
    public void removeOldTeams(Department department, List<Long> newIds) {
        department.getTeams().removeIf(team -> !newIds.contains(team.getId()));
    }

    @Transactional
    public void updateOrAddTeam(Department databaseDepartment, Team updatedTeam) {
        Optional<Team> existingTeamOpt = databaseDepartment.getTeams().stream()
                .filter(team -> team.getId().equals(updatedTeam.getId()))
                .findFirst();

        if (existingTeamOpt.isPresent()) {
            updateTeam(existingTeamOpt.get(), updatedTeam);
        } else {
            addNewTeam(databaseDepartment, updatedTeam);
        }
    }

    @Transactional
    public void updateTeam(Team databaseTeam, Team updatedTeam) {
        updateProject(databaseTeam, updatedTeam.getProject());
        teamRepository.save(databaseTeam);
    }

    @Transactional
    public void addNewTeam(Department databaseDepartment, Team updatedTeam) {
        updateProject(null, updatedTeam.getProject());
        Team savedTeam = teamRepository.save(updatedTeam);
        databaseDepartment.getTeams().add(savedTeam);
    }

    @Transactional
    public void updateProject(Team databaseTeam, Project updatedProject) {
        if (databaseTeam == null) {
            updateManager(null, updatedProject.getManager());
            projectRepository.save(updatedProject);
        } else {
            Project databaseProject = databaseTeam.getProject();
            updateManager(databaseProject, updatedProject.getManager());
            projectRepository.save(databaseProject);
        }
    }

    @Transactional
    public void updateManager(Project databaseProject, Manager updatedManager) {
        if (databaseProject == null) {
            managerRepository.save(updatedManager);
        } else {
            Manager databaseManager = databaseProject.getManager();
            databaseManager.setContactInfo(updatedManager.getContactInfo());
            managerRepository.save(databaseManager);
        }
    }


    @Transactional
    public void deleteCompany(Long companyId) {

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException("Company with id " + companyId + " not found"));
        company.getDepartments().forEach(department -> {
            department.getTeams().forEach(team -> {
                managerRepository.deleteById(team.getProject().getManager().getId());
                projectRepository.deleteById(team.getProject().getId());
                teamRepository.deleteById(team.getId());

            });
            departmentRepository.deleteById(department.getId());
        });
        companyRepository.deleteById(companyId);
    }
}
