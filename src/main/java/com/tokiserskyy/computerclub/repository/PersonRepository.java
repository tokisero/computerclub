package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    Predicate<Person> findByName(String username);

    List<Person> getAllByName(String name);

    @Query("SELECT DISTINCT p FROM Person p " +
            "LEFT JOIN FETCH p.bookings b " +
            "LEFT JOIN FETCH b.computer c")
    List<Person> findAllWithBookingsAndComputers();

    @Query("SELECT DISTINCT p FROM Person p " +
            "LEFT JOIN FETCH p.bookings b " +
            "LEFT JOIN FETCH b.computer c " +
            "WHERE p.id = :id")
    Optional<Person> findByIdWithBookingsAndComputers(@Param("id") int id);

}
