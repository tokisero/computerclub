package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT s FROM Booking s WHERE s.startTime >= CURRENT_DATE")
    List<Booking> getBookingsFromToday();

    @Query("SELECT b FROM Booking b WHERE b.computer.id = :computerId")
    List<Booking> getAllByComputerId(@Param("computerId") int computerId);

    @Query(value = "SELECT * FROM bookings WHERE user_id = :personId", nativeQuery = true)
    List<Booking> getAllByPersonId(@Param("personId") int personId);

    @Query("SELECT b FROM Booking b WHERE b.person.name = :personName")
    List<Booking> getAllBookingsByPersonName(@Param("personName") String personName);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.computer.id = :computerId
          AND (
                (:startTime BETWEEN b.startTime AND b.endTime)
             OR (:endTime BETWEEN b.startTime AND b.endTime)
             OR (b.startTime BETWEEN :startTime AND :endTime)
             OR (b.endTime BETWEEN :startTime AND :endTime)
          )
    """)
    List<Booking> findOverlappingBookings(@Param("computerId") int computerId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
}
