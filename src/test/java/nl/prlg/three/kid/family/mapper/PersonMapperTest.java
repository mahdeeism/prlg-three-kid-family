package nl.prlg.three.kid.family.mapper;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonMapperTest {

    @Test
    public void toPersonDtoTest() {
        var person = new Person();
        person.setId(1L);
        person.setName("John Doe");
        person.setDateOfBirth(LocalDate.now());
        person.setPartnerId(2L);
        person.setParent1Id(3L);
        person.setParent2Id(4L);
        person.setChildIds(List.of(5L, 6L, 7L, 8L, 9L));

        var result = PersonMapper.toPersonDto(person);

        assertEquals(result.getId(), person.getId());
        assertEquals(result.getName(), person.getName());
        assertEquals(result.getBirthDate(), person.getDateOfBirth().toString());
        assertEquals(result.getPartner().getId(), person.getPartnerId());
        assertEquals(result.getParent1().getId(), person.getParent1Id());
        assertEquals(result.getParent2().getId(), person.getParent2Id());
        assertThat(result.getChildren()).hasSize(5).extracting(PersonDto::getId).containsExactly(5L, 6L, 7L, 8L, 9L);
    }

    @Test
    public void toPersonTest() {
        var personDto = new PersonDto();
        personDto.setId(1L);
        personDto.setName("John Doe");
        personDto.setBirthDate(LocalDate.now().toString());

        var parent1Dto = new PersonDto();
        parent1Dto.setId(1L);
        var parent2Dto = new PersonDto();
        parent2Dto.setId(2L);
        var partnerDto = new PersonDto();
        partnerDto.setId(3L);

        personDto.setParent1(parent1Dto);
        personDto.setParent2(parent2Dto);
        personDto.setPartner(partnerDto);

        var child1Dto = new PersonDto();
        child1Dto.setId(8L);
        var child2Dto = new PersonDto();
        child2Dto.setId(9L);

        personDto.setChildren(List.of(child1Dto, child2Dto));

        var result = PersonMapper.toPerson(personDto);

        assertEquals(result.getId(), personDto.getId());
        assertEquals(result.getName(), personDto.getName());
        assertEquals(result.getPartnerId(), personDto.getPartner().getId());
        assertEquals(result.getParent1Id(), personDto.getParent1().getId());
        assertEquals(result.getParent2Id(), personDto.getParent2().getId());
        assertEquals(result.getChildIds(), personDto.getChildren().stream().map(PersonDto::getId).toList());
    }
}
