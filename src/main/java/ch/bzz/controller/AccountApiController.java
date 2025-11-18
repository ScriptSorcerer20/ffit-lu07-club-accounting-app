package ch.bzz.controller;

import lombok.extern.slf4j.Slf4j;

import ch.bzz.repository.AccountRepository;
import ch.bzz.dataclasses.Project;
import ch.bzz.generated.api.AccountApi;
import ch.bzz.generated.model.Account;
import ch.bzz.generated.model.AccountUpdate;
import ch.bzz.generated.model.UpdateAccountsRequest;
import ch.bzz.repository.ProjectRepository;
import ch.bzz.util.JwtUtil;
import jakarta.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class AccountApiController implements AccountApi {
    private final JwtUtil jwt;
    private final AccountRepository accountRespository;
    private final EntityManager entityManager;

    public AccountApiController(JwtUtil jwtUtil, AccountRepository accountRepository, EntityManager entityManager) {
        this.jwt = jwtUtil;
        this.accountRespository = accountRepository;
        this.entityManager = entityManager;
        log.info("AccountApiController initialized");
    }

    @Override
    public ResponseEntity<List<Account>> getAccounts() {
        String projectName = jwt.verifyTokenAndExtractSubject();
        log.debug("subject from header: {}", projectName);

        List<ch.bzz.dataclasses.Account> accounts = accountRespository.findByProject_ProjectName(projectName);
        log.debug("accounts from database: {}", accounts);

        List<ch.bzz.generated.model.Account> apiAccounts = accounts.stream()
                .map(dbAcc -> {
                    ch.bzz.generated.model.Account apiAcc = new ch.bzz.generated.model.Account();
                    apiAcc.setNumber(dbAcc.getAccountNumber());
                    apiAcc.setName(dbAcc.getName());
                    return apiAcc;
                })
                .toList();
        if (apiAccounts.isEmpty()) {
            apiAccounts = new ArrayList<>(apiAccounts);
            apiAccounts.add(new Account(1000, "Kasse"));
            apiAccounts.add(new Account(1020, "Bank"));
        }

        return ResponseEntity.ok(apiAccounts);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateAccounts(UpdateAccountsRequest updateAccountsRequest) {
        String projectName = jwt.verifyTokenAndExtractSubject();
        log.debug("subject from header: {}", projectName);

        // Get Accounts (list) from Request
        List<AccountUpdate> updates = updateAccountsRequest.getAccounts();

        if (updates == null || updates.isEmpty()) {
            log.info("No accounts provided for update for project {}", projectName);
            return ResponseEntity.noContent().build();
        }

        // Project project = accountRespository.findProjectByProjectName(projectName);
        // if (project == null) {
        // log.error("Project {} not found", projectName);
        // return ResponseEntity.notFound().build();
        // }

        for (AccountUpdate update : updates) {
            Integer accountNumber = update.getNumber();
            String accountName = update.getName().get();

            log.debug("Processing account update: number={} name={} project={}", accountNumber, accountName,
                    projectName);

            try {
                if (accountName == null || accountName == null) {
                    // Delete account
                    accountRespository.deleteByAccountNumberAndProject_ProjectName(accountNumber, projectName);
                    log.debug("Deleted account: {} (project={})", accountName, projectName);

                    // if (accountOpt.isPresent()) {
                    // accountRespository.deleteByAccountNumberAndProject_ProjectName(accountNumber,
                    // projectName);
                    // } else {
                    // log.warn("No account found to delete: {} (project={})", accountName,
                    // projectName);
                    // }

                } else {
                    log.debug("accountName should not be null: '{}'", accountName);
                    Optional<ch.bzz.dataclasses.Account> accountOpt = accountRespository
                            .findByAccountNumberAndProject_ProjectName(accountNumber, projectName);
                    // Todo Update or create account

                    ch.bzz.dataclasses.Account account;
                    if (accountOpt.isPresent()) {
                        account = accountOpt.get();
                        log.debug("Updating existing account: {} ({})", accountNumber, accountName);
                    } else {
                        Project projectRef = entityManager.getReference(Project.class, projectName);
                        account = new ch.bzz.dataclasses.Account();
                        log.debug("Creating new account: {} ({})", accountNumber, accountName);
                        account.setAccountNumber(accountNumber);
                        account.setProject(projectRef);
                    }
                    account.setName(accountName);

                    accountRespository.save(account);
                    log.debug("Saved account: {} ({})", accountNumber, accountName);
                }
            } catch (Exception e) {
                log.error("Error processing account update for number={} project={}. Rolling back.", accountNumber,
                        projectName, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception occured", e);
            }
        }
        return ResponseEntity.noContent().build();
    }
}