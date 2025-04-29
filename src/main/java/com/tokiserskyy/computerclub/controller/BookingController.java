package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.BookingDto;
import com.tokiserskyy.computerclub.dto.BookingUpdateDto;
import com.tokiserskyy.computerclub.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable int id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getAllBookingsByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByUserId(userId));
    }

    @GetMapping("/computer/{computerId}")
    public ResponseEntity<List<BookingDto>> getAllBookingsByComputerId(@PathVariable int computerId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByComputerId(computerId));
    }

    @GetMapping("/person")
    public ResponseEntity<List<BookingDto>> getAllBookingsByPersonName(@RequestParam String name) {
        return ResponseEntity.ok(bookingService.getAllBookingsByPersonName(name));
    }

    @PostMapping("/add")
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingUpdateDto booking
    ) {
        BookingDto saved = bookingService.addBooking(booking);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDto> updateBooking(
            @Valid @RequestBody BookingUpdateDto dto,
            @PathVariable int id
            ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }
}
