package nl.prlg.three.kid.family.controller;

import nl.prlg.three.kid.family.dto.PersonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ApiController {

    @PostMapping
    ResponseEntity<List<PersonDto>> savePerson(@RequestBody PersonDto personDto);
}
