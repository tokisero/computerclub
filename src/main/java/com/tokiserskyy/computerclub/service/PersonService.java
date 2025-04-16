package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.mapper.PersonMapper;
import com.tokiserskyy.computerclub.model.Person;
import java.util.List;

import com.tokiserskyy.computerclub.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    public List<PersonDto> getAllPersons() {
        return personRepository.findAllWithBookingsAndComputers()
                .stream()
                .map(PersonMapper::toDto)
                .toList();
    }

    public PersonDto getPersonById(int id) {
        return personRepository.findByIdWithBookingsAndComputers(id)
                .map(PersonMapper::toDto)
                .orElse(null);
    }

    public Person getPersonEntityById(int id) {
        return personRepository.findById(id).orElse(null);
    }

    public List<PersonDto> getPersonByName(String name) {
        return personRepository.getAllByName(name)
                .stream()
                .map(PersonMapper::toDto)
                .toList();
    }

    public PersonDto registerPerson(Person person) {
        return PersonMapper.toDtoShallow(personRepository.save(person));
    }

    public void deletePersonById(int id) {
        personRepository.deleteById(id);
    }
}
