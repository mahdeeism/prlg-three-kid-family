package nl.prlg.three.kid.family.repository;

import nl.prlg.three.kid.family.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
