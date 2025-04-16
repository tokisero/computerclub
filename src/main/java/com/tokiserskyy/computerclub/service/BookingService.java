package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.BookingDto;
import com.tokiserskyy.computerclub.mapper.BookingMapper;
import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.model.Booking;
import com.tokiserskyy.computerclub.repository.BookingRepository;
import com.tokiserskyy.computerclub.repository.ComputerRepository;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final PersonRepository personRepository;
    private final ComputerRepository computerRepository;

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    public List<BookingDto> getAllBookingsByComputerId(int computerId) {
        return bookingRepository.getAllByComputerId(computerId)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    public List<BookingDto> getAllBookingsByUserId(int personId) {
        Optional<Person> personOpt = personRepository.findById(personId);
        if (personOpt.isEmpty()) return List.of();

        return bookingRepository.getAllByPersonId(personId)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    public BookingDto getBookingById(int id) {
        return bookingRepository.findById(id)
                .map(BookingMapper::toDto)
                .orElse(null);
    }

    @Transactional
    public BookingDto addBooking(int userId, int computerId, Booking booking) {
        Person person = personRepository.findById(userId).orElse(null);
        Computer computer = computerRepository.findById(computerId).orElse(null);
        if (person == null || computer == null) return null;

        booking.setPerson(person);
        booking.setComputer(computer);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    public List<BookingDto> getBookingsFromToday() {
        return bookingRepository.getBookingsFromToday()
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    public void deleteBookingById(int id) {
        bookingRepository.deleteById(id);
    }
}
