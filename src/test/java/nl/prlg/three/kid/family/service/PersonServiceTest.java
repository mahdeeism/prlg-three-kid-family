package nl.prlg.three.kid.family.service;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;
import nl.prlg.three.kid.family.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @InjectMocks
    PersonService personService;

    @Mock
    PersonRepository personRepository;

    @Test
    void isUnder18Success() {
        var person = new Person();
        person.setDateOfBirth(LocalDate.now().minusYears(17));

        var result = personService.isUnder18(person);

        assertTrue(result);
    }

    @Test
    void isUnder18Fail() {
        var person = new Person();
        person.setDateOfBirth(LocalDate.now().minusYears(19));

        var result = personService.isUnder18(person);

        assertFalse(result);
    }

    @Test
    void validateKidsWithSamePartnerAndOneUnder18Success() {
        var person = new Person();
        person.setPartnerId(1L);
        person.setChildIds(List.of(2L, 3L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(1L);
        child1.setDateOfBirth(LocalDate.now().minusYears(17));

        var child2 = new Person();
        child2.setId(3L);
        child2.setParent2Id(1L);
        child2.setDateOfBirth(LocalDate.now().minusYears(19));

        when(personRepository.findAllById(any())).thenReturn(List.of(child1, child2));

        var result = personService.validateKidsWithSamePartnerAndOneUnder18(person);

        assertTrue(result);
    }

    @Test
    void validateKidsWithSamePartnerAndOneUnder18Fail() {
        var person = new Person();
        person.setPartnerId(1L);
        person.setChildIds(List.of(2L, 3L, 4L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(1L);
        child1.setDateOfBirth(LocalDate.now().minusYears(21));

        var child2 = new Person();
        child2.setId(3L);
        child2.setParent2Id(1L);
        child2.setDateOfBirth(LocalDate.now().minusYears(19));

        var child3 = new Person();
        child3.setId(4L);
        child3.setParent2Id(1L);
        child3.setDateOfBirth(LocalDate.now().minusYears(23));

        when(personRepository.findAllById(any())).thenReturn(List.of(child1, child2, child3));

        var result = personService.validateKidsWithSamePartnerAndOneUnder18(person);

        assertFalse(result);
    }

    @Test
    void validateKidsWithSamePartnerAndOneUnder18DifferentParent() {
        var person = new Person();
        person.setPartnerId(1L);
        person.setChildIds(List.of(2L, 3L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(7L);
        child1.setDateOfBirth(LocalDate.now().minusYears(17));

        var child2 = new Person();
        child2.setId(3L);
        child2.setParent2Id(1L);
        child2.setDateOfBirth(LocalDate.now().minusYears(19));

        when(personRepository.findAllById(any())).thenReturn(List.of(child1, child2));

        var result = personService.validateKidsWithSamePartnerAndOneUnder18(person);

        assertFalse(result);
    }

    @Test
    void findThreeKidFamiliesSuccess() {
        var person1 = new Person();
        person1.setPartnerId(1L);
        person1.setChildIds(List.of(2L, 3L, 4L));

        var person2 = new Person();
        person2.setChildIds(List.of(5L, 6L, 7l));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(1L);
        child1.setDateOfBirth(LocalDate.now().minusYears(21));

        var child2 = new Person();
        child2.setId(3L);
        child2.setParent2Id(1L);
        child2.setDateOfBirth(LocalDate.now().minusYears(19));

        var child3 = new Person();
        child3.setId(4L);
        child3.setParent2Id(1L);
        child3.setDateOfBirth(LocalDate.now().minusYears(17));

        when(personRepository.findAll()).thenReturn(List.of(person1, person2));
        when(personRepository.findAllById(List.of(2L, 3L, 4L))).thenReturn(List.of(child1, child2, child3));

        var result = personService.findThreeKidFamilies();

        assertThat(result).hasSize(1).extracting(Person::getChildIds).containsExactly(List.of(2L, 3L, 4L));
    }

    @Test
    void findThreeKidFamiliesFail() {
        var person1 = new Person();
        person1.setPartnerId(1L);
        person1.setChildIds(List.of(2L, 3L));

        var person2 = new Person();
        person1.setPartnerId(9L);
        person2.setChildIds(List.of(5L, 6L));

        when(personRepository.findAll()).thenReturn(List.of(person1, person2));

        var result = personService.findThreeKidFamilies();

        assertThat(result).hasSize(0);
    }

    @Test
    void enforceBidirectionalIntegrityChildNoParents() {
        var person = new Person();
        person.setId(1L);
        person.setChildIds(List.of(2L));

        var child = new Person();
        child.setId(2L);

        when(personRepository.findAllById(any())).thenReturn(List.of(child));

        personService.enforceBidirectionalIntegrity(person);

        verify(personRepository, times(1)).save(child);
    }

    @Test
    void enforceBidirectionalIntegrityChildHasParent1() {
        var person = new Person();
        person.setId(1L);
        person.setChildIds(List.of(2L));

        var child = new Person();
        child.setId(2L);
        child.setParent1Id(1L);

        when(personRepository.findAllById(any())).thenReturn(List.of(child));

        personService.enforceBidirectionalIntegrity(person);

        verify(personRepository, times(1)).save(child);
    }

    @Test
    void enforceBidirectionalIntegrityChildHasParent2() {
        var person = new Person();
        person.setId(1L);
        person.setChildIds(List.of(2L));

        var child = new Person();
        child.setId(2L);
        child.setParent2Id(1L);

        when(personRepository.findAllById(any())).thenReturn(List.of(child));

        personService.enforceBidirectionalIntegrity(person);

        verify(personRepository, times(1)).save(child);
    }

    @Test
    void enforceBidirectionalIntegrityChildHasBothParents() {
        var person = new Person();
        person.setId(1L);
        person.setChildIds(List.of(2L));

        var child = new Person();
        child.setId(2L);
        child.setParent1Id(1L);
        child.setParent2Id(3L);

        when(personRepository.findAllById(any())).thenReturn(List.of(child));

        personService.enforceBidirectionalIntegrity(person);

        verify(personRepository, times(0)).save(child);
    }

    @Test
    void savePersonSuccess() {
        var personDto = new PersonDto(1L);
        personDto.setBirthDate(LocalDate.now().minusYears(34).toString());

        var person = new Person();
        person.setId(1L);
        person.setPartnerId(2L);
        person.setChildIds(List.of(3L, 4L, 5L));
        person.setDateOfBirth(LocalDate.now().minusYears(34));

        var parent2 = new Person();
        parent2.setId(2L);

        var child1 = new Person();
        child1.setId(3L);
        child1.setParent1Id(1L);
        child1.setParent2Id(2L);
        child1.setDateOfBirth(LocalDate.now().minusYears(17));

        var child2 = new Person();
        child2.setId(4L);
        child2.setParent1Id(1L);
        child2.setParent2Id(2L);
        child2.setDateOfBirth(LocalDate.now().minusYears(18));

        var child3 = new Person();
        child3.setId(5L);
        child3.setParent1Id(1L);
        child3.setParent2Id(2L);
        child3.setDateOfBirth(LocalDate.now().minusYears(19));

        when(personRepository.save(any())).thenReturn(person);
        when(personRepository.findAllById(List.of(3L, 4L, 5L))).thenReturn(List.of(child1, child2, child3));
        when(personRepository.findAll()).thenReturn(List.of(person, parent2, child1, child2, child3));

        var result = personService.savePerson(personDto);

        assertThat(result).hasSize(1).extracting(PersonDto::getId).containsOnly(1L);
    }

    @Test
    void savePersonMultipleMatch() {
        var personDto = new PersonDto(1L);
        personDto.setBirthDate(LocalDate.now().minusYears(34).toString());

        var person = new Person();
        person.setId(1L);
        person.setPartnerId(2L);
        person.setChildIds(List.of(3L, 4L, 5L));
        person.setDateOfBirth(LocalDate.now().minusYears(34));

        var parent2 = new Person();
        parent2.setId(2L);
        parent2.setPartnerId(1L);
        parent2.setChildIds(List.of(3L, 4L, 5L));
        parent2.setDateOfBirth(LocalDate.now().minusYears(32));

        var child1 = new Person();
        child1.setId(3L);
        child1.setParent1Id(1L);
        child1.setParent2Id(2L);
        child1.setDateOfBirth(LocalDate.now().minusYears(17));

        var child2 = new Person();
        child2.setId(4L);
        child2.setParent1Id(1L);
        child2.setParent2Id(2L);
        child2.setDateOfBirth(LocalDate.now().minusYears(18));

        var child3 = new Person();
        child3.setId(5L);
        child3.setParent1Id(1L);
        child3.setParent2Id(2L);
        child3.setDateOfBirth(LocalDate.now().minusYears(19));

        when(personRepository.save(any())).thenReturn(person);
        when(personRepository.findAllById(List.of(3L, 4L, 5L))).thenReturn(List.of(child1, child2, child3));
        when(personRepository.findAll()).thenReturn(List.of(person, parent2, child1, child2, child3));

        var result = personService.savePerson(personDto);

        assertThat(result).hasSize(2).extracting(PersonDto::getId).containsOnly(1L, 2L);
    }

    @Test
    void savePersonWithoutPartner() {
        var personDto = new PersonDto(1L);
        personDto.setBirthDate(LocalDate.now().minusYears(34).toString());

        var person = new Person();
        person.setId(1L);
        person.setChildIds(List.of(3L, 4L, 5L));
        person.setDateOfBirth(LocalDate.now().minusYears(34));

        var parent2 = new Person();
        parent2.setId(2L);

        var child1 = new Person();
        child1.setId(3L);
        child1.setParent1Id(1L);
        child1.setParent2Id(2L);
        child1.setDateOfBirth(LocalDate.now().minusYears(17));

        var child2 = new Person();
        child2.setId(4L);
        child2.setParent1Id(1L);
        child2.setParent2Id(2L);
        child2.setDateOfBirth(LocalDate.now().minusYears(18));

        var child3 = new Person();
        child3.setId(5L);
        child3.setParent1Id(1L);
        child3.setParent2Id(2L);
        child3.setDateOfBirth(LocalDate.now().minusYears(19));

        when(personRepository.save(any())).thenReturn(person);
        when(personRepository.findAllById(List.of(3L, 4L, 5L))).thenReturn(List.of(child1, child2, child3));
        when(personRepository.findAll()).thenReturn(List.of(person, parent2, child1, child2, child3));

        var result = personService.savePerson(personDto);

        assertThat(result).hasSize(0);
    }

    @Test
    void savePersonWithOnlyTwoKids() {
        var personDto = new PersonDto(1L);
        personDto.setBirthDate(LocalDate.now().minusYears(34).toString());

        var person = new Person();
        person.setId(1L);
        person.setPartnerId(2L);
        person.setChildIds(List.of(3L, 4L));
        person.setDateOfBirth(LocalDate.now().minusYears(34));

        var parent2 = new Person();
        parent2.setId(2L);

        var child1 = new Person();
        child1.setId(3L);
        child1.setParent1Id(1L);
        child1.setParent2Id(2L);
        child1.setDateOfBirth(LocalDate.now().minusYears(17));

        var child2 = new Person();
        child2.setId(4L);
        child2.setParent1Id(1L);
        child2.setParent2Id(2L);
        child2.setDateOfBirth(LocalDate.now().minusYears(18));

        when(personRepository.save(any())).thenReturn(person);
        when(personRepository.findAllById(List.of(3L, 4L))).thenReturn(List.of(child1, child2));
        when(personRepository.findAll()).thenReturn(List.of(person, parent2, child1, child2));

        var result = personService.savePerson(personDto);

        assertThat(result).hasSize(0);
    }

    @Test
    void savePersonNoUnder18s() {
        var personDto = new PersonDto(1L);
        personDto.setBirthDate(LocalDate.now().minusYears(34).toString());

        var person = new Person();
        person.setId(1L);
        person.setPartnerId(2L);
        person.setChildIds(List.of(3L, 4L, 5L));
        person.setDateOfBirth(LocalDate.now().minusYears(34));

        var parent2 = new Person();
        parent2.setId(2L);

        var child1 = new Person();
        child1.setId(3L);
        child1.setParent1Id(1L);
        child1.setParent2Id(2L);
        child1.setDateOfBirth(LocalDate.now().minusYears(18));

        var child2 = new Person();
        child2.setId(4L);
        child2.setParent1Id(1L);
        child2.setParent2Id(2L);
        child2.setDateOfBirth(LocalDate.now().minusYears(18));

        var child3 = new Person();
        child3.setId(5L);
        child3.setParent1Id(1L);
        child3.setParent2Id(2L);
        child3.setDateOfBirth(LocalDate.now().minusYears(18));

        when(personRepository.save(any())).thenReturn(person);
        when(personRepository.findAllById(List.of(3L, 4L, 5L))).thenReturn(List.of(child1, child2, child3));
        when(personRepository.findAll()).thenReturn(List.of(person, parent2, child1, child2, child3));

        var result = personService.savePerson(personDto);

        assertThat(result).hasSize(0);
    }
}
