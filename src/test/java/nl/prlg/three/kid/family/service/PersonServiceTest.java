package nl.prlg.three.kid.family.service;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;
import nl.prlg.three.kid.family.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void savePersonSingleMatch() {
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

    @Test
    void enforceBidirectionalIntegrityParentWithoutPersistedChildren() {
        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);

        var parent = new Person();
        parent.setId(1L);
        parent.setChildIds(List.of(2L, 3L));

        personService.enforceBidirectionalIntegrity(parent);

        verify(personRepository, times(1)).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertEquals(2L, captor.getValue().get(0).getId());
        assertEquals(3L, captor.getValue().get(1).getId());
    }

    @Test
    void enforceBidirectionalIntegrityParent1WithPersistedChild() {
        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);

        var parent = new Person();
        parent.setId(4L);
        parent.setChildIds(List.of(2L, 3L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(1L);

        when(personRepository.findById(2L)).thenReturn(Optional.of(child1));

        personService.enforceBidirectionalIntegrity(parent);

        verify(personRepository, times(1)).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertEquals(2L, captor.getValue().get(0).getId());
        assertEquals(4L, captor.getValue().get(0).getParent2Id());
        assertEquals(3L, captor.getValue().get(1).getId());
    }

    @Test
    void enforceBidirectionalIntegrityParent2WithPersistedChild() {
        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);

        var parent = new Person();
        parent.setId(4L);
        parent.setChildIds(List.of(2L, 3L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent2Id(1L);

        when(personRepository.findById(2L)).thenReturn(Optional.of(child1));

        personService.enforceBidirectionalIntegrity(parent);

        verify(personRepository, times(1)).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertEquals(2L, captor.getValue().get(0).getId());
        assertEquals(4L, captor.getValue().get(0).getParent1Id());
        assertEquals(3L, captor.getValue().get(1).getId());
    }

    @Test
    void shouldUpdateParent1Success() {
        var parent = new Person();
        parent.setId(1L);

        var child = new Person();
        child.setId(2L);
        child.setParent2Id(3L);

        var result = personService.shouldUpdateParent1(parent, child);

        assertTrue(result);
    }

    @Test
    void shouldUpdateParent1Fail() {
        var parent = new Person();
        parent.setId(1L);

        var child = new Person();
        child.setId(2L);
        child.setParent1Id(1L);

        var result = personService.shouldUpdateParent1(parent, child);

        assertFalse(result);
    }

    @Test
    void shouldUpdateParent1FailBothParentsDefined() {
        var parent = new Person();
        parent.setId(1L);

        var child = new Person();
        child.setId(2L);
        child.setParent1Id(5L);
        child.setParent2Id(6L);

        var result = personService.shouldUpdateParent1(parent, child);

        assertFalse(result);
    }

    @Test
    void shouldUpdateParent1SuccessChildHasNoParents() {
        var parent = new Person();
        parent.setId(1L);

        var child = new Person();
        child.setId(2L);

        var result = personService.shouldUpdateParent1(parent, child);

        assertTrue(result);
    }

    @Test
    void shouldUpdateParent2Success() {
        var parent = new Person();
        parent.setId(1L);

        var child = new Person();
        child.setId(2L);
        child.setParent1Id(3L);

        var result = personService.shouldUpdateParent2(parent, child);

        assertTrue(result);
    }

    @Test
    void shouldUpdateParent2Fail() {
        var parent = new Person();
        parent.setId(1L);

        var child = new Person();
        child.setId(2L);
        child.setParent2Id(1L);

        var result = personService.shouldUpdateParent2(parent, child);

        assertFalse(result);
    }

    @Test
    void getChildrenToUpdatePersonWithNoKids() {
        var person = new Person();
        person.setId(4L);

        var result = personService.getChildrenToUpdate(person);

        assertThat(result).isEmpty();
    }

    @Test
    void getChildrenToUpdatePersonWithChildWithoutParent2() {
        var parent = new Person();
        parent.setId(5L);
        parent.setChildIds(List.of(2L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(1L);

        when(personRepository.findById(2L)).thenReturn(Optional.of(child1));

        var result = personService.getChildrenToUpdate(parent);

        assertThat(result).hasSize(1);
        assertEquals(5L, result.getFirst().getParent2Id());
    }

    @Test
    void getChildrenToUpdatePersonWithTwoKidsWhereOneIsAlreadyPersistedWithParent1() {
        var parent = new Person();
        parent.setId(1L);
        parent.setChildIds(List.of(2L, 3L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent1Id(1L);

        when(personRepository.findById(2L)).thenReturn(Optional.of(child1));

        var result = personService.getChildrenToUpdate(parent);

        assertThat(result).hasSize(1);
    }

    @Test
    void getChildrenToUpdatePersonWithTwoKidsWhereOneIsAlreadyPersistedWithParent2() {
        var parent = new Person();
        parent.setId(1L);
        parent.setChildIds(List.of(2L, 3L));

        var child1 = new Person();
        child1.setId(2L);
        child1.setParent2Id(1L);

        when(personRepository.findById(2L)).thenReturn(Optional.of(child1));

        var result = personService.getChildrenToUpdate(parent);

        assertThat(result).hasSize(1);
    }

    @Test
    void getChildrenToUpdatePersonWithTwoKidsWhereNoneArePersisted() {
        var parent = new Person();
        parent.setId(1L);
        parent.setChildIds(List.of(2L, 3L));

        var result = personService.getChildrenToUpdate(parent);

        assertThat(result).hasSize(2);
        assertEquals(2L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }

    @Test
    void getParentsToUpdatePersistNewParent1() {
        var person = new Person();
        person.setId(1L);
        person.setParent1Id(2L);
        person.setParent2Id(3L);

        List<Long> childIds = new ArrayList<>();
        childIds.add(1L);
        childIds.add(5L);

        var parent2 = new Person();
        parent2.setId(3L);
        parent2.setChildIds(childIds);

        when(personRepository.findById(2L)).thenReturn(Optional.of(new Person()));
        when(personRepository.findById(3L)).thenReturn(Optional.of(parent2));

        var result = personService.getParentsToUpdate(person);

        assertThat(result).hasSize(1);
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getParentsToUpdatePersistNewParent2() {
        var person = new Person();
        person.setId(1L);
        person.setParent1Id(3L);
        person.setParent2Id(2L);

        List<Long> childIds = new ArrayList<>();
        childIds.add(1L);
        childIds.add(5L);

        var parent1 = new Person();
        parent1.setId(3L);
        parent1.setChildIds(childIds);

        when(personRepository.findById(2L)).thenReturn(Optional.of(new Person()));
        when(personRepository.findById(3L)).thenReturn(Optional.of(parent1));

        var result = personService.getParentsToUpdate(person);

        assertThat(result).hasSize(1);
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getParentsToUpdateNoParents() {
        var person = new Person();
        person.setId(1L);

        var result = personService.getParentsToUpdate(person);

        assertThat(result).hasSize(0);
    }
}
