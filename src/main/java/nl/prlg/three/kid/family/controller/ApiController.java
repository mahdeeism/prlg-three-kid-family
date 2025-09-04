package nl.prlg.three.kid.family.controller;

import nl.prlg.three.kid.family.dto.PersonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ApiController {

    /**
     * This function saves the incoming PersonDto and will determine if any records stored
     * in the database satifies the conditions of the Three Kid Family
     *
     * @param personDto - the PersonDto to be saved
     * @return a list of suitable matches abiding by the rules of the Three Kid Family Challenge
     */
    @PostMapping
    ResponseEntity<List<PersonDto>> savePerson(@RequestBody PersonDto personDto);
}
