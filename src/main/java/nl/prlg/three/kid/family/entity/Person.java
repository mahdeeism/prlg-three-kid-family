package nl.prlg.three.kid.family.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.List;

@Entity(name="person")
public class Person {

    @Id
    private Long id;

    private String name;

    private LocalDate dateOfBirth;

    private Long parent1Id;

    private Long parent2Id;

    private Long partnerId;

    private List<Long> childIds;
}
