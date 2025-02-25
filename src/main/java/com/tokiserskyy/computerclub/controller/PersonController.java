package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.service.PersonService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Person>> searchUsers(@RequestParam String name) {
        List<Person> persons = personService.getPersonByName(name);
        return persons.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable int id) {
        Optional<Person> person = personService.getPersonById(id);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
