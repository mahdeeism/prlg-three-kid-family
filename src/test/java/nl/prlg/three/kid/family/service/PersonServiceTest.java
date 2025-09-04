package nl.prlg.three.kid.family.service;

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
}
