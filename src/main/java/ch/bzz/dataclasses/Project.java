package ch.bzz.dataclasses;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter // generiert Getter
@Setter // generiert Setter
@NoArgsConstructor // generiert Standard-Konstruktor
@AllArgsConstructor // generiert Konstruktor mit allen Feldern
@Entity
public class Project {
    @Id
    private String projectName;
    private String passwordHash;
}
