package nl.prlg.three.kid.family.service;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;
import nl.prlg.three.kid.family.mapper.PersonMapper;
import nl.prlg.three.kid.family.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        var children = personRepository.findAllById(person.getChildIds());
        children.forEach(child -> {
            boolean needsUpdate = false;

            if (child.getParent1Id() == null && child.getParent2Id() == null) {
                child.setParent1Id(person.getId());
                needsUpdate = true;
            }
            else if (child.getParent1Id() != null && child.getParent2Id() == null) {
                child.setParent2Id(person.getId());
                needsUpdate = true;
            }
            else if (child.getParent1Id() == null && child.getParent2Id() != null) {
                child.setParent1Id(person.getId());
                needsUpdate = true;
            }

            if (needsUpdate) {
                personRepository.save(child);
            }
        });
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
        return LocalDate.now().getYear() - person.getDateOfBirth().getYear() < 18;
    }
}
