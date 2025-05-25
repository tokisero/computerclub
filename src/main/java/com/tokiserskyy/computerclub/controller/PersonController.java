package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.PersonDto;
import com.tokiserskyy.computerclub.service.PersonService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/persons")
@Tag(name = "Persons", description = "Endpoints for managing persons")
public class PersonController {
    private final PersonService personService;

    @GetMapping
    @Operation(summary = "Get all persons")
    public List<PersonDto> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get person by ID")
    public PersonDto getPersonById(@PathVariable int id) {
        return personService.getPersonById(id);
    }

    @PostMapping("/add")
    @Operation(summary = "Register new person")
    public PersonDto registerPerson(@Valid @RequestBody PersonDto personDto) {
        return personService.registerPerson(personDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update person by ID")
    public PersonDto updatePerson(@PathVariable int id, @Valid @RequestBody PersonDto personDetails) {
        return personService.updatePerson(id, personDetails);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete person by ID")
    public void deletePersonById(@PathVariable int id) {
        personService.deletePersonById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search person by name")
    public ResponseEntity<List<PersonDto>> searchPersons(@RequestParam String name) {
        List<PersonDto> persons = personService.getPersonByName(name);
        return persons.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(persons);
    }

    @PatchMapping("/{id}/role")
    public PersonDto changeRole(
            @PathVariable int id,
            @RequestParam int newRole) {
        return personService.changeRole(id, newRole);
    }
}
