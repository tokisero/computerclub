package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.model.Person;
import java.util.List;

import com.tokiserskyy.computerclub.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAllWithBookingsAndComputers();
    }

    public Person getPersonById(int id) {
        return personRepository.findByIdWithBookingsAndComputers(id).orElse(null);
    }

    public List<Person> getPersonByName(String name) {
        return personRepository.getAllByName(name);
    }

    public Person registerPerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePersonById(int id) {
        personRepository.deleteById(id);
    }
}
