package nl.prlg.three.kid.family.service;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.mapper.PersonMapper;
import nl.prlg.three.kid.family.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<PersonDto> savePerson(PersonDto personDto) {
        var person = PersonMapper.toPerson(personDto);
        person = personRepository.save(person);
        return List.of(PersonMapper.toPersonDto(person));
    }
}
