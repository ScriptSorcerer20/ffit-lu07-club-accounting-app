package ch.bzz.repository;

import ch.bzz.dataclasses.Account;
import ch.bzz.dataclasses.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Automatisch abgeleitete Methode:
    List<Account> findByProject(Project project);
}
