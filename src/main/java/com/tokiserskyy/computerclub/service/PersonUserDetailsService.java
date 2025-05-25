package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PersonUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (person.getUsername() == null || person.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Person username cannot be null or empty");
        }
        if (person.getPassword() == null || person.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Person password cannot be null or empty");
        }

        String role = person.getRole() == 1 ? "ROLE_ADMIN" : "ROLE_USER";
        return new User(
                person.getUsername(),
                person.getPassword(),
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
        );
    }
}