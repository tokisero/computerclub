package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Computer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ComputerRepository extends JpaRepository<Computer, Integer>, JpaSpecificationExecutor<Computer> {
    @Query("SELECT DISTINCT c FROM Computer c " +
            "LEFT JOIN FETCH c.bookings b " +
            "LEFT JOIN FETCH b.person p")
    List<Computer> findAllWithBookingsAndPersons();

    @Query("SELECT DISTINCT c FROM Computer c " +
            "LEFT JOIN FETCH c.bookings b " +
            "LEFT JOIN FETCH b.person p " +
            "WHERE c.id = :id")
    Optional<Computer> findByIdWithBookingsAndPersons(@Param("id") int id);

    Computer getComputersById(int id);
}
