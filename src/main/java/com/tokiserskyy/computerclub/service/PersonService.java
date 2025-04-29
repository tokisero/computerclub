package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.mapper.PersonMapper;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.exception.NotFoundException;
import java.util.List;

import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {
    private static final String PERSON_NOT_FOUND = "Person not found";

    private final PersonRepository personRepository;

    @Transactional
    public List<PersonDto> getAllPersons() {
        return personRepository.findAllWithBookingsAndComputers()
                .stream()
                .map(PersonMapper::toDto)
                .toList();
    }

    @Transactional
    public PersonDto getPersonById(int id) {
        return personRepository.findByIdWithBookingsAndComputers(id)
                .map(PersonMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Person with ID " + id + " not found."));
    }

    @Transactional
    public Person getPersonEntityById(int id) {
        return personRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<PersonDto> getPersonByName(String name) {
        return personRepository.getAllByName(name)
                .stream()
                .map(PersonMapper::toDto)
                .toList();
    }

    @Transactional
    public PersonDto registerPerson(PersonDto personDto) {
        if (personDto.getPassword() == null || personDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is empty.");
        }
        if (personRepository.existsByEmail(personDto.getEmail())) {
            throw new IllegalArgumentException("This email is already in use.");
        }

        if (personRepository.existsByUsername(personDto.getUsername())) {
            throw new IllegalArgumentException("This username is already in use.");
        }
        Person person = new Person();
        person.setName(personDto.getName());
        person.setEmail(personDto.getEmail());
        person.setUsername(personDto.getUsername());
        person.setPassword(personDto.getPassword());
        return PersonMapper.toDtoShallow(personRepository.save(person));
    }

    @Transactional
    public void deletePersonById(int id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public PersonDto updatePerson(int id, PersonDto personDetails) {
        Person person = personRepository.findById(id).orElseThrow(()
                -> new NotFoundException(PERSON_NOT_FOUND));

        if(!person.getUsername().equals(personDetails.getUsername()) &&
                personRepository.existsByUsername(personDetails.getUsername())) {
            throw new IllegalArgumentException("This username is already in use.");
        }

        if(!person.getEmail().equals(personDetails.getEmail()) &&
                personRepository.existsByEmail(personDetails.getEmail())) {
            throw new IllegalArgumentException("This email is already in use.");
        }

        person.setName(personDetails.getName());
        person.setUsername(personDetails.getUsername());
        person.setEmail(personDetails.getEmail());

        if(personDetails.getPassword() != null && !personDetails.getPassword().isEmpty()) {
            person.setPassword(personDetails.getPassword());
        }
        return PersonMapper.toDtoWithPassword(personRepository.save(person));
    }
}
