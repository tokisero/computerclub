package com.tokiserskyy.computerclub.mapper;

import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.model.Person;

@UtilityClass
public class PersonMapper {
    public PersonDto toDtoShallow(Person person) {
        if (person == null) return null;
        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setUsername(person.getUsername());
        dto.setEmail(person.getEmail());
        return dto;
    }

    public PersonDto toDto(Person person) {
        if (person == null) return null;
        PersonDto dto = toDtoShallow(person);
        dto.setBookings(person.getBookings().stream()
                .map(BookingMapper::toDtoWithoutPersons)
                .collect(Collectors.toList()));
        return dto;
    }


    public Person toEntity(PersonDto dto) {
        if (dto == null) return null;
        Person person = new Person();
        person.setId(dto.getId());
        person.setName(dto.getName());
        person.setUsername(dto.getUsername());
        person.setEmail(dto.getEmail());
        return person;
    }
}
