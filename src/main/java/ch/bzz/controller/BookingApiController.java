package ch.bzz.controller;

import lombok.extern.slf4j.Slf4j;
import ch.bzz.dataclasses.Project;
import ch.bzz.generated.api.BookingApi;
import ch.bzz.generated.model.Booking;
import ch.bzz.generated.model.BookingUpdate;
import ch.bzz.generated.model.UpdateBookingsRequest;
import ch.bzz.repository.BookingRepository;
import ch.bzz.util.JwtUtil;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class BookingApiController implements BookingApi {
    private final JwtUtil jwt;
    private final BookingRepository bookingRespository;

    public BookingApiController(JwtUtil jwtUtil, BookingRepository bookingRepository) {
        this.jwt = jwtUtil;
        this.bookingRespository = bookingRepository;
        log.info("BookingApiController initialized");
    }

    @Override
    public ResponseEntity<List<Booking>> getBookings() {
        String projectName = jwt.verifyTokenAndExtractSubject();
        log.debug("subject from header: {}", projectName);

        // Project project = bookingRespository.findByProject_ProjectName(projectName);

        List<ch.bzz.dataclasses.Booking> bookings = bookingRespository.findByProject_ProjectName(projectName);
        log.debug("accounts from database: {}", bookings);

        List<ch.bzz.generated.model.Booking> apiBookings = bookings.stream()
                .map(dbBook -> {
                    ch.bzz.generated.model.Booking apiBook = new ch.bzz.generated.model.Booking();

                    apiBook.setNumber(dbBook.getId());
                    apiBook.setDate(dbBook.getDate());
                    apiBook.setText(dbBook.getText());

                    if (dbBook.getDebitAccount() != null)
                        apiBook.setDebit(dbBook.getDebitAccount().getAccountNumber());

                    if (dbBook.getCreditAccount() != null)
                        apiBook.setCredit(dbBook.getCreditAccount().getAccountNumber());

                    if (dbBook.getAmount() != null)
                        apiBook.setAmount(dbBook.getAmount());

                    return apiBook;
                })
                .toList();

        return ResponseEntity.ok(apiBookings);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateBookings(UpdateBookingsRequest updateBookingsRequest) {

        // String projectName = jwt.verifyTokenAndExtractSubject();
        // log.debug("subject from header: {}", projectName);

        // List<BookingUpdate> updates = updateBookingsRequest.getEntries();
        // if (updates == null || updates.isEmpty()) {
        // log.info("No bookings provided for update for project {}", projectName);
        // return ResponseEntity.noContent().build();
        // }

        // Project project = bookingRespository.findByProject_ProjectName(projectName);
        // if (project == null) {
        // log.error("Project {} not found", projectName);
        // return ResponseEntity.notFound().build();
        // }

        // for (BookingUpdate update : updates) {

        // Integer bookingId = update.getId();
        // JsonNullable<LocalDate> date = update.getDate();
        // JsonNullable<String> text = update.getText();
        // JsonNullable<Integer> debitNumber = update.getDebit();
        // JsonNullable<Integer> creditNumber = update.getCredit();
        // JsonNullable<Float> amountFloat = update.getAmount();

        // log.debug("Processing booking update: id={} text={} project={}",
        // bookingId, text, projectName);

        // try {
        // if (bookingId == null) {
        // // DELETE
        // Optional<ch.bzz.dataclasses.Booking> toDelete = bookingRespository
        // .findByIdAndProject_ProjectName(bookingId, projectName);

        // if (toDelete.isPresent()) {
        // bookingRespository.deleteByIdAndProject_ProjectName(bookingId, projectName);

        // log.debug("Deleted booking with id {} (project={})", update.getId(),
        // projectName);
        // }

        // continue;
        // }

        // // CREATE or UPDATE

        // Optional<ch.bzz.dataclasses.Booking> existing =
        // bookingRespository.findByIdAndProject_ProjectName(bookingId, projectName);

        // ch.bzz.dataclasses.Booking entity;

        // if (existing.isPresent()) {
        // entity = existing.get();
        // log.debug("Updating existing booking {}", bookingId);
        // } else {
        // entity = new ch.bzz.dataclasses.Booking();
        // entity.setId(bookingId);(bookingId);
        // entity.setProject(project);
        // log.debug("Creating new booking {}", bookingId);
        // }

        // // Load debit and credit accounts
        // ch.bzz.dataclasses.Account debit = bookingRespository
        // .findByAccountNumberAndProject_ProjectName(debitNumber, projectName)
        // .orElseThrow(() -> new RuntimeException("Debit account not found: " +
        // debitNumber));

        // ch.bzz.dataclasses.Account credit = bookingRespository
        // .findByAccountNumberAndProject_ProjectName(creditNumber, projectName)
        // .orElseThrow(() -> new RuntimeException("Credit account not found: " +
        // creditNumber));

        // // Map API → DB
        // entity.setDate(date);
        // entity.setAmount(amountFloat.intValue());
        // entity.setDebitAccount(debit);
        // entity.setCreditAccount(credit);

        // bookingRepository.save(entity);

        // log.debug("Saved booking {} for project {}", bookingId, projectName);

        // } catch (Exception e) {
        // log.error("Error updating booking id={} → rollback", bookingId, e);
        // throw e;
        // }
        // }

        // log.info("All bookings updated successfully for project {}", projectName);
        // return ResponseEntity.noContent().build();
        // }

        return ResponseEntity.noContent().build();
    }
}
