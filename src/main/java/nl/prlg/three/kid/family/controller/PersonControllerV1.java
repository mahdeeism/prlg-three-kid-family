package nl.prlg.three.kid.family.controller;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
public class PersonControllerV1 implements ApiController {

    private PersonService personService;

    public PersonControllerV1(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public ResponseEntity<List<PersonDto>> savePerson(PersonDto personDto) {
        var matchedPeople = personService.savePerson(personDto);
        return matchedPeople.isEmpty() ? ResponseEntity.status(444).build() : ResponseEntity.ok(matchedPeople);
    }
}
