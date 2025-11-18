package ch.bzz.controller;

import lombok.extern.slf4j.Slf4j;

import ch.bzz.dataclasses.Project;
import ch.bzz.generated.api.ProjectApi;
import ch.bzz.generated.model.LoginProject200Response;
import ch.bzz.generated.model.LoginRequest;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ProjectApiController implements ProjectApi {

    private final ProjectRepository projectRepository;
    private final JwtUtil jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ProjectApiController(ProjectRepository projectRepository, JwtUtil jwtUtil) {
        this.projectRepository = projectRepository;
        this.jwt = jwtUtil;
        log.info("ProjectApiController initialized");
    }

    @Override
    public ResponseEntity<Void> createProject(LoginRequest loginRequest) {
        log.debug("Request to create project '{}'", loginRequest.getProjectName());
        Optional<Project> optionalProject = projectRepository.findById(loginRequest.getProjectName());
        if (optionalProject.isPresent()) {
            log.warn("Project '{}' already exists", loginRequest.getProjectName());
            return ResponseEntity.status(400).build();
        } else {
            Project newProject = new Project();
            newProject.setProjectName(loginRequest.getProjectName());
            String password = loginRequest.getPassword();
            String hashedPassword = encoder.encode(password);
            newProject.setPasswordHash(hashedPassword);
            projectRepository.save(newProject);

            log.info("Project '{}' created successfully", loginRequest.getProjectName());
            return ResponseEntity.status(200).build();
        }

    }

    @Override
    public ResponseEntity<LoginProject200Response> loginProject(LoginRequest loginRequest) {
        log.debug("Login attempt for project '{}'", loginRequest.getProjectName());
        Optional<Project> optionalProject = projectRepository.findById(loginRequest.getProjectName());
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            String storedHash = project.getPasswordHash();
            if (encoder.matches(loginRequest.getPassword(), storedHash)) {
                LoginProject200Response response = new LoginProject200Response();
                String token = jwt.generateToken(project.getProjectName());
                response.setAccessToken(token);

                log.info("Login successful for project '{}'", loginRequest.getProjectName());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Login failed for project '{}': wrong password", loginRequest.getProjectName());
                return ResponseEntity.status(401).build();
            }
        }
        log.warn("Login failed: project '{}' not found", loginRequest.getProjectName());
        return ResponseEntity.status(401).build();
    }
}
