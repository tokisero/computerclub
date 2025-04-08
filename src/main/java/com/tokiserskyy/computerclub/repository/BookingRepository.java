package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT s FROM Booking s WHERE s.startTime >= CURRENT_DATE")
    List<Booking> getBookingsFromToday();

    List<Booking> getAllByComputerId(int computerId);

    List<Booking> getAllByPersonId(int personId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.computer c " +
            "JOIN FETCH b.person p")
    List<Booking> findAllWithComputerAndPerson();

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.computer c " +
            "JOIN FETCH b.person p " +
            "WHERE b.id = :id")
    Optional<Booking> findByIdWithComputerAndPerson(@Param("id") int id);
}
