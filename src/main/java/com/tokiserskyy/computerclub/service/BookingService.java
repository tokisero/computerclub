package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.repository.BookingRepository;
import com.tokiserskyy.computerclub.model.Booking;
import com.tokiserskyy.computerclub.repository.ComputerRepository;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private PersonRepository personRepository;
    private ComputerRepository computerRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAllWithComputerAndPerson();
    }

    public List<Booking> getAllBookingsByComputerId(int computerId) {
        return bookingRepository.getAllByComputerId(computerId);
    }

    public List<Booking> getAllBookingsByUserId(int personId) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person != null) return bookingRepository.getAllByPersonId(personId);
        return null;

    }

    public Booking getBookingById(int id) {
        return bookingRepository.findByIdWithComputerAndPerson(id).orElse(null);
    }

    @Transactional
    public Booking addBooking(int userId,int computerId, Booking booking) {
        Person person = personRepository.findById(userId).orElse(null);
        Computer computer = computerRepository.findById(computerId).orElse(null);
        if (person == null || computer == null) return null;
        booking.setPerson(person);
        booking.setComputer(computer);
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsFromToday() {
        return bookingRepository.getBookingsFromToday();
    }

    public void deleteBookingById(int id) {
        bookingRepository.deleteById(id);
    }
}
