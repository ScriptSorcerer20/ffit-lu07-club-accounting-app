package ch.bzz.repository;

import ch.bzz.dataclasses.Booking;
import ch.bzz.dataclasses.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Alle Buchungen eines bestimmten Projekts abrufen
    List<Booking> findByProject(Project project);
}
