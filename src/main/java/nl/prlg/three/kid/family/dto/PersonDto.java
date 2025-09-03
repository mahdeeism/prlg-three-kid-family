package nl.prlg.three.kid.family.dto;

import java.util.List;

public class PersonDto {
    private Long id;
    private String name;
    private String birthDate;
    private PersonDto parent1;
    private PersonDto parent2;
    private PersonDto partner;
    private List<PersonDto> children;

    public PersonDto () {}

    public PersonDto (Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public PersonDto getParent1() {
        return parent1;
    }

    public void setParent1(PersonDto parent1) {
        this.parent1 = parent1;
    }

    public PersonDto getParent2() {
        return parent2;
    }

    public void setParent2(PersonDto parent2) {
        this.parent2 = parent2;
    }

    public PersonDto getPartner() {
        return partner;
    }

    public void setPartner(PersonDto partner) {
        this.partner = partner;
    }

    public List<PersonDto> getChildren() {
        return children;
    }

    public void setChildren(List<PersonDto> children) {
        this.children = children;
    }
}
