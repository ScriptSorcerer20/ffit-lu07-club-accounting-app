package ch.bzz.controller;

import lombok.extern.slf4j.Slf4j;
import ch.bzz.dataclasses.Account;
import ch.bzz.dataclasses.Project;
import ch.bzz.generated.api.BookingApi;
import ch.bzz.generated.model.Booking;
import ch.bzz.generated.model.BookingUpdate;
import ch.bzz.generated.model.UpdateBookingsRequest;
import ch.bzz.repository.AccountRepository;
import ch.bzz.repository.BookingRepository;
import ch.bzz.util.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class BookingApiController implements BookingApi {
    private final JwtUtil jwt;
    private final BookingRepository bookingRespository;
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    public BookingApiController(JwtUtil jwtUtil, BookingRepository bookingRepository, EntityManager entityManager,
            AccountRepository accountRepository) {
        this.jwt = jwtUtil;
        this.bookingRespository = bookingRepository;
        this.entityManager = entityManager;
        this.accountRepository = accountRepository;
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

        String projectName = jwt.verifyTokenAndExtractSubject();
        log.debug("subject from header: {}", projectName);

        List<BookingUpdate> updates = updateBookingsRequest.getEntries();

        if (updates == null || updates.isEmpty()) {
            log.info("No bookings provided for update for project {}", projectName);
            return ResponseEntity.noContent().build();
        }

        for (BookingUpdate update : updates) {

            Integer bookingId = update.getId();
            LocalDate date = update.getDate().get();
            String text = update.getText().get();
            Integer debitNumber = update.getDebit().get();
            Integer creditNumber = update.getCredit().get();
            Float amountFloat = update.getAmount().get();

            log.debug("Processing booking update: id={} text={} project={}", bookingId, text, projectName);

            try {
                if (text == null) {
                    bookingRespository.deleteByIdAndProject_ProjectName(bookingId, projectName);
                    log.debug("Deleted booking with id {} (project={})", bookingId, projectName);
                } else {
                    log.debug("accountName should not be null: '{}'", text);
                    Optional<ch.bzz.dataclasses.Booking> existing = bookingRespository
                            .findByIdAndProject_ProjectName(bookingId, projectName);

                    Project projectRef = entityManager.getReference(Project.class, projectName);
                    ch.bzz.dataclasses.Booking entity;
                    if (existing.isPresent()) {
                        entity = existing.get();
                        log.debug("Updating existing booking {}", bookingId);
                    } else {
                        // Project projectRef = entityManager.getReference(Project.class, projectName);
                        entity = new ch.bzz.dataclasses.Booking();
                        entity.setBookingNumber(bookingId);
                        entity.setProject(projectRef);
                        log.debug("Creating new booking {}", bookingId);
                    }

                    Account debit = accountRepository.findByAccountNumberAndProject(debitNumber, projectRef)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Debit Account not found"));

                    Account credit = accountRepository.findByAccountNumberAndProject(creditNumber, projectRef)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Debit Account not found"));

                    entity.setDate(date);
                    entity.setText(text);
                    entity.setAmount(amountFloat);
                    entity.setDebitAccount(debit);
                    entity.setCreditAccount(credit);

                    bookingRespository.save(entity);
                    log.debug("Saved booking {} for project {}", bookingId, projectName);
                }
            } catch (Exception e) {
                log.error("Error processing account update for id={} project={}. Rolling back.", bookingId,
                        projectName, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occured", e);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
