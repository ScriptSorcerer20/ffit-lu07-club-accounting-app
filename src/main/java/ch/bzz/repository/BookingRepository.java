package ch.bzz.repository;

import ch.bzz.dataclasses.Account;
import ch.bzz.dataclasses.Booking;
import ch.bzz.dataclasses.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Alle Buchungen eines bestimmten Projekts abrufen
    List<Booking> findByProject(Project project);

    // Project findByProject_ProjectName(String projectName);

    Optional<Booking> findByIdAndProject_ProjectName(Integer id, String projectName);

    List<Booking> findByProject_ProjectName(String projectName);

    void deleteByIdAndProject_ProjectName(Integer id, String projectName);

    // Account findByAccountNumberAndProject_ProjectName(Integer accountNumber,
    // String projectName);
}
