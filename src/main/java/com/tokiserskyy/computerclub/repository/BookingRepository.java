package com.tokiserskyy.computerclub.repository;

import com.tokiserskyy.computerclub.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT s FROM Booking s WHERE s.startTime >= CURRENT_DATE")
    List<Booking> getBookingsFromToday();

    List<Booking> getAllByComputerId(int computerId);

    List<Booking> getAllByPersonId(int personId);
}
