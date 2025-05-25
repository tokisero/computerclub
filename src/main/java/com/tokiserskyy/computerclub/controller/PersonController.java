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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping("/me")
    public ResponseEntity<PersonDto> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        PersonDto personDto = personService.getPersonByUsername(username);

        return ResponseEntity.ok(personDto);
    }


    // В PersonController
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @personService.getPersonEntityById(#id)?.username") // Только админ или сам пользователь
    public ResponseEntity<PersonDto> updatePerson(@PathVariable int id, @RequestBody PersonDto personDto) {
        PersonDto updatedPerson = personService.updatePerson(id, personDto);
        return ResponseEntity.ok(updatedPerson);
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
