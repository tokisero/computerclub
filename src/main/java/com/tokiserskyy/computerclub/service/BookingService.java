package com.tokiserskyy.computerclub.service;

import com.tokiserskyy.computerclub.dto.BookingDto;
import com.tokiserskyy.computerclub.dto.BookingUpdateDto;
import com.tokiserskyy.computerclub.exception.BadRequestException;
import com.tokiserskyy.computerclub.exception.NotFoundException;
import com.tokiserskyy.computerclub.mapper.BookingMapper;
import com.tokiserskyy.computerclub.model.Booking;
import com.tokiserskyy.computerclub.model.Computer;
import com.tokiserskyy.computerclub.model.Person;
import com.tokiserskyy.computerclub.repository.BookingRepository;
import com.tokiserskyy.computerclub.repository.ComputerRepository;
import com.tokiserskyy.computerclub.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ComputerRepository computerRepository;
    private final PersonRepository personRepository;
    private final PersonService personService;
    private final ComputerService computerService;
    private static final String BOOKING_WITH_ID = "booking with id ";

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookingDto getBookingById(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(BOOKING_WITH_ID + id));
        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getAllBookingsByUserId(int userId) {
        return bookingRepository.getAllByPersonId(userId).stream()
                .map(BookingMapper::toDtoWithoutComputers)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookingsByComputerId(int computerId) {
        return bookingRepository.getAllByComputerId(computerId).stream()
                .map(BookingMapper::toDtoWithoutPersons)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookingsByPersonName(String name) {
        return bookingRepository.getAllBookingsByPersonName(name).stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto addBooking(BookingUpdateDto booking) {
        if (booking.getStartTime() == null || booking.getEndTime() == null) {
            throw new BadRequestException("Start and end time must be provided.");
        }

        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new BadRequestException("Start time cannot be after end time.");
        }

        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                booking.getComputerId(), booking.getStartTime(), booking.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new BadRequestException("This time slot is already booked for the selected computer.");
        }

        Person person = personService.getPersonEntityById(booking.getPersonId());
        if (person == null) {
            throw new NotFoundException("User not found");
        }

        Computer computer = computerService.getComputerEntityById(booking.getComputerId());
        if (computer == null) {
            throw new NotFoundException("Computer not found");
        }
        Booking booking1 = new Booking();

        booking1.setStartTime(booking.getStartTime());
        booking1.setEndTime(booking.getEndTime());
        booking1.setPerson(person);
        booking1.setComputer(computer);

        Booking saved = bookingRepository.save(booking1);
        return BookingMapper.toDto(saved);
    }

    @Transactional
    public BookingDto updateBooking(int bookingId, BookingUpdateDto dto) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        if (dto.getComputerId() != null) {
            List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                    dto.getComputerId(), dto.getStartTime(), dto.getEndTime()
            );

            if (!overlaps.isEmpty()) {
                throw new BadRequestException("This time slot is already booked for the selected computer.");
            }

            Computer computer = computerRepository.findById(dto.getComputerId())
                    .orElseThrow(() -> new NotFoundException("Computer not found"));
            existingBooking.setComputer(computer);
        }

        if (dto.getPersonId() != null) {
            Person person = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new NotFoundException("Person not found"));
            existingBooking.setPerson(person);
        }

        existingBooking.setStartTime(dto.getStartTime());
        existingBooking.setEndTime(dto.getEndTime());

        return BookingMapper.toDto(bookingRepository.save(existingBooking));
    }


    @Transactional
    public void deleteBookingById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException(BOOKING_WITH_ID + id + " does not exist");
        }
        bookingRepository.deleteById(id);
    }
}
