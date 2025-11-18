package ch.bzz.dataclasses;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter // generiert Getter
@Setter // generiert Setter
@NoArgsConstructor // generiert Standard-Konstruktor
@AllArgsConstructor // generiert Konstruktor mit allen Feldern
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer bookingNumber;
    private LocalDate date;
    private String text;
    @ManyToOne
    @JoinColumn(name = "debitAccountId", nullable = false)
    private Account debitAccount;
    @ManyToOne
    @JoinColumn(name = "creditAccountId", nullable = false)
    private Account creditAccount;
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "projectName", nullable = false)
    private Project project;
}
