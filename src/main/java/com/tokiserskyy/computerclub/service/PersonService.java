package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private final List<Person> persons = new ArrayList<>();

    public PersonService() {
        persons.add(new Person(777, "Vlad", "vlad1234x",
                        "vlad1234x@gmail.com", 2006));
        persons.add(new Person(1337, "Nikita", "tokiserskyy",
                        "nikita07nfsmw@gmail.com", 2006));
        persons.add(new Person(228, "Phillip", "shhknnq_",
                        "shhknnq@gmail.com", 2007));
        persons.add(new Person(123, "Artem", "fuckingtranquility",
                        "fuckingtranquility@gmail.com", 2007));
        persons.add(new Person(231, "Kostya", "bnshygettingguap",
                        "kostyaFomch@gmail.com", 2006));
        persons.add(new Person(323, "Vlad", "vladosik",
                        "vladosik@gmail.com", 2005));
    }

    public List<Person> getPersonByName(String username) {
        return persons.stream()
                .filter(person -> person.getName().equalsIgnoreCase(username))
                .toList();
    }

    public Optional<Person> getPersonById(int id) {
        return persons.stream()
                .filter(person -> person.getId() == id)
                .findFirst();
    }
}
