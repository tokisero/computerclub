package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.BookingDto;
import com.tokiserskyy.computerclub.model.Booking;
import com.tokiserskyy.computerclub.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/api/bookings")
    public List<BookingDto> getAllBooking() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/api/persons/{userId}/bookings")
    public List<BookingDto> getAllPersonBookings(@PathVariable int userId) {
        return bookingService.getAllBookingsByUserId(userId);
    }

    @GetMapping("/api/bookings/{id}")
    public BookingDto getBookingById(@PathVariable int id){
        return bookingService.getBookingById(id);
    }

    @GetMapping("/api/computers/{computerId}/bookings")
    public List<BookingDto> getAllComputerBookings(@PathVariable int computerId) {
        return bookingService.getAllBookingsByComputerId(computerId);
    }

    @PostMapping("/api/persons/{userId}/book")
    public BookingDto addBooking(@RequestBody Booking booking, @PathVariable int userId,
                              @RequestParam int computerId) {
        return bookingService.addBooking(userId, computerId, booking);
    }

    @Transactional
    @DeleteMapping("/api/bookings/{id}")
    public void deleteBooking(@PathVariable int id){
        bookingService.deleteBookingById(id);
    }
}
