package ch.bzz.repository;

import ch.bzz.dataclasses.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    // keine zusätzlichen Methoden notwendig

    // Project findProjectByProjectName(String projectName);
}
