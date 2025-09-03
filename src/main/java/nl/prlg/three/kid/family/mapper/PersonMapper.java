package nl.prlg.three.kid.family.mapper;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;

import java.time.LocalDate;

public class PersonMapper {

    public static PersonDto toPersonDto(Person person) {
        var personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setName(person.getName());
        personDto.setBirthDate(person.getDateOfBirth().toString());

        var parent1 = new PersonDto();
        parent1.setId(person.getParent1Id());

        var parent2 = new PersonDto();
        parent2.setId(person.getParent2Id());

        var partner = new PersonDto();
        partner.setId(person.getPartnerId());

        personDto.setPartner(partner);
        personDto.setParent1(parent1);
        personDto.setParent2(parent2);

        personDto.setChildren(
            person.getChildIds().stream()
                .map(PersonDto::new)
                .toList()
        );

        return personDto;
    }

    public static Person toPerson(PersonDto personDto) {
        var person = new Person();
        person.setId(personDto.getId());
        person.setName(personDto.getName());
        person.setDateOfBirth(LocalDate.parse(personDto.getBirthDate()));
        person.setParent1Id(personDto.getParent1().getId());
        person.setParent2Id(personDto.getParent2().getId());
        person.setPartnerId(personDto.getPartner().getId());
        person.setChildIds(personDto.getChildren().stream().map(PersonDto::getId).toList());

        return person;
    }
}
