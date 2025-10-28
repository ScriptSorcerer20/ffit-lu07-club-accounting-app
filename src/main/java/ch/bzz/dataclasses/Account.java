package ch.bzz.dataclasses;

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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private Integer accountNumber;
    private String name;
    @ManyToOne
    @JoinColumn(name = "project_name", nullable = false)
    private Project project;
}
