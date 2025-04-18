package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import com.tokiserskyy.computerclub.service.PersonService;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/persons")
public class PersonController {
    private final PersonService personService;
    private final PersonRepository personRepository;

    @GetMapping
    public List<PersonDto> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public PersonDto getPersonById(@PathVariable int id) {
        return personService.getPersonById(id);
    }

    @PostMapping("/add")
    public PersonDto registerPerson(@RequestBody Person person) {
        return personService.registerPerson(person);
    }

    @PutMapping("/{id}")
    public Person updatePerson(@PathVariable int id, @RequestBody Person personDetails) {
        Person person = personService.getPersonEntityById(id);
        if (person != null) {
            person.setName(personDetails.getName());
            person.setUsername(personDetails.getUsername());
            person.setPassword(personDetails.getPassword());
            person.setEmail(personDetails.getEmail());
            return personRepository.save(person);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deletePersonById(@PathVariable int id) {
        personService.deletePersonById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDto>> searchPersons(@RequestParam String name) {
        List<PersonDto> persons = personService.getPersonByName(name);
        return persons.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(persons);
    }
}
