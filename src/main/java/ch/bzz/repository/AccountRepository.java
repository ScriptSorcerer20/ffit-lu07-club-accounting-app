package ch.bzz.repository;

import ch.bzz.dataclasses.Account;
import ch.bzz.dataclasses.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Automatisch abgeleitete Methode:
    List<Account> findByProject(Project project);

    List<Account> findByProject_ProjectName(String projectName);

    void deleteByAccountNumberAndProject_ProjectName(Integer accountNumber, String projectName);

    Optional<Account> findByAccountNumberAndProject_ProjectName(Integer accountNumber, String projectName);

    Optional<Account> findByAccountNumberAndProject(Integer accountNumber, Project project);
}
