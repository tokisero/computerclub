package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import com.tokiserskyy.computerclub.service.PersonService;
import java.util.List;

import jakarta.validation.Valid;
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
    public PersonDto registerPerson(@Valid @RequestBody PersonDto personDto) {
        return personService.registerPerson(personDto);
    }

    @PutMapping("/{id}")
    public PersonDto updatePerson(@PathVariable int id, @Valid @RequestBody PersonDto personDetails) {
        return personService.updatePerson(id, personDetails);
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
