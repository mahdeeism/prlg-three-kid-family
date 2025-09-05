package nl.prlg.three.kid.family.service;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;
import nl.prlg.three.kid.family.mapper.PersonMapper;
import nl.prlg.three.kid.family.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PersonService {

    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<PersonDto> savePerson(PersonDto personDto) {
        var person = personRepository.save(PersonMapper.toPerson(personDto));
        enforceBidirectionalIntegrity(person);

        return findThreeKidFamilies().stream().map(PersonMapper::toPersonDto).toList();
    }

    void enforceBidirectionalIntegrity(Person person) {
        List<Person> personsToSave = new ArrayList<>();
        personsToSave.addAll(getChildrenToUpdate(person));
        personsToSave.addAll(getParentsToUpdate(person));

        personRepository.saveAll(personsToSave);
    }

    List<Person> getChildrenToUpdate (Person person) {
        List<Person> personsToSave = new ArrayList<>();
        person.getChildIds().forEach(childId -> {
            var child = personRepository.findById(childId).orElse(new Person());
            child.setId(childId);

            if (shouldUpdateParent2(person, child)) {
                child.setParent2Id(person.getId());
                personsToSave.add(child);
            }
            else if (shouldUpdateParent1(person, child)) {
                child.setParent1Id(person.getId());
                personsToSave.add(child);
            }
        });

        return personsToSave;
    }

    List<Person> getParentsToUpdate (Person person) {
        List<Person> personsToSave = new ArrayList<>();

        var parent1 = person.getParent1Id() != null ? personRepository.findById(person.getParent1Id()).orElse(new Person()) : null;
        var parent2 = person.getParent2Id() != null ? personRepository.findById(person.getParent2Id()).orElse(new Person()) : null;

        if (parent1 != null && !parent1.getChildIds().contains(person.getId())) {
            parent1.getChildIds().add(person.getId());
            parent1.setId(person.getParent1Id());
            personsToSave.add(parent1);
        }

        if (parent2 != null && !parent2.getChildIds().contains(person.getId())) {
            parent2.getChildIds().add(person.getId());
            parent2.setId(person.getParent2Id());
            personsToSave.add(parent2);
        }

        return personsToSave;
    }

    List<Person> findThreeKidFamilies() {
        var allPeople = personRepository.findAll();

        return allPeople.stream()
                .filter(person -> person.getPartnerId() != null && person.getChildIds().size() == 3)
                .filter(this::validateKidsWithSamePartnerAndOneUnder18).toList();
    }

    boolean validateKidsWithSamePartnerAndOneUnder18(Person person) {
        var children = personRepository.findAllById(person.getChildIds());

        return children.stream().anyMatch(this::isUnder18) && children.stream().allMatch(child ->
                Objects.equals(child.getParent1Id(), person.getPartnerId()) || Objects.equals(child.getParent2Id(), person.getPartnerId()));
    }

    boolean isUnder18(Person person) {
        return person.getDateOfBirth() != null && LocalDate.now().getYear() - person.getDateOfBirth().getYear() < 18;
    }

    boolean shouldUpdateParent1(Person person, Person child) {
        return (child.getParent1Id() == null && child.getParent2Id() == null) ||
                (child.getParent1Id() == null && child.getParent2Id() != null && child.getParent2Id() != person.getId());
    }

    boolean shouldUpdateParent2(Person person, Person child) {
        return child.getParent1Id() != null && child.getParent2Id() == null && child.getParent1Id() != person.getId();
    }
}
