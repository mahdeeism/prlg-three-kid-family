package nl.prlg.three.kid.family.integration;

import nl.prlg.three.kid.family.dto.PersonDto;
import nl.prlg.three.kid.family.entity.Person;
import nl.prlg.three.kid.family.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerV1Test {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    PersonRepository personRepository;

    @BeforeEach
    public void setup() {
        var parent1 = new Person();
        parent1.setId(1L);
        parent1.setName("Amy Apples");
        parent1.setPartnerId(2L);
        parent1.setChildIds(List.of(3L, 4L));

        var parent2 = new Person();
        parent2.setId(2L);
        parent2.setName("Charl Cantaloupes");
        parent2.setPartnerId(1L);
        parent2.setChildIds(List.of(3L, 4L));

        var child1 = new Person();
        child1.setId(3L);
        child1.setName("Bob Bananas");
        child1.setParent1Id(2L);
        child1.setParent2Id(1L);
        child1.setDateOfBirth(LocalDate.now().minusYears(20));

        var child2 = new Person();
        child2.setId(4L);
        child2.setName("Donna Durians");
        child2.setParent1Id(2L);
        child2.setParent2Id(1L);
        child2.setDateOfBirth(LocalDate.now().minusYears(20));

        personRepository.saveAll(List.of(parent1, parent2, child1, child2));
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAll();
    }

    @Test
    void testSavePersonNoMatchesReturns444() {
        var person = new PersonDto();
        person.setId(5L);
        person.setName("Amy Apples");
        person.setBirthDate("1985-01-01");

        webTestClient
                .post()
                .uri("/api/v1/people")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(person)
                .exchange()
                .expectStatus().isEqualTo(444);
    }

    @Test
    void testSavePersonMatchesReturns200() {
        var personRequest = new PersonDto();
        personRequest.setId(5L);
        personRequest.setName("Elia Elderberries");
        personRequest.setParent1(new PersonDto(1L));
        personRequest.setParent2(new PersonDto(2L));
        personRequest.setBirthDate(LocalDate.now().minusYears(15).toString());

        var result = webTestClient
                .post()
                .uri("/api/v1/people")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personRequest)
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBodyList(PersonDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(2)
                .extracting(PersonDto::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }
}
