package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.mapper.PersonMapper;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.exception.NotFoundException;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {
    private static final String PERSON_WITH_ID = "Person with id ";

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

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
                .orElseThrow(() -> new NotFoundException(PERSON_WITH_ID + id));
    }

    @Transactional
    public Person getPersonEntityById(int id) {
        return personRepository.findById(id).orElse(null);
    }

    @Transactional
    public PersonDto getPersonByUsername(String username) {
        log.info("Fetching person by username: {}", username);
        return personRepository.findByUsername(username)
                .map(PersonMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Person with username {} not found", username);
                    return new NotFoundException("Person with username " + username + " not found");
                });
    }

    @Transactional
    public List<PersonDto> getPersonByName(String name) {
        return personRepository.getAllByName(name)
                .stream()
                .map(PersonMapper::toDto)
                .toList();
    }

    @Transactional
    public PersonDto changeRole(int id, int role) {
        try {
            if (role < 0) {
                throw new IllegalArgumentException("Role cannot be negative");
            }
            Person person = personRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(PERSON_WITH_ID + id + " not found"));
            person.setRole(role);
            return PersonMapper.toDto(personRepository.save(person));
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to change role for person with id " + id + ": " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public PersonDto registerPerson(PersonDto personDto) {
        log.info("Registering person with username: {}", personDto.getUsername());
        if (personDto.getPassword() == null || personDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
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
        person.setPassword(passwordEncoder.encode(personDto.getPassword()));
        person.setRole(0);
        return PersonMapper.toDtoShallow(personRepository.save(person));
    }

    @Transactional
    public void deletePersonById(int id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public PersonDto updatePerson(int id, PersonDto personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PERSON_WITH_ID + id));

        if (!person.getUsername().equals(personDetails.getUsername()) &&
                personRepository.existsByUsername(personDetails.getUsername())) {
            throw new IllegalArgumentException("This username is already in use.");
        }
        if (!person.getEmail().equals(personDetails.getEmail()) &&
                personRepository.existsByEmail(personDetails.getEmail())) {
            throw new IllegalArgumentException("This email is already in use.");
        }

        person.setName(personDetails.getName());
        person.setUsername(personDetails.getUsername());
        person.setEmail(personDetails.getEmail());
        if (personDetails.getPassword() != null && !personDetails.getPassword().isEmpty()) {
            person.setPassword(passwordEncoder.encode(personDetails.getPassword()));
        }
        return PersonMapper.toDtoWithPassword(personRepository.save(person));
    }
}