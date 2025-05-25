package com.tokiserskyy.computerclub.controller;

import com.tokiserskyy.computerclub.dto.BookingDto;
import com.tokiserskyy.computerclub.dto.BookingUpdateDto;
import com.tokiserskyy.computerclub.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Endpoints for managing bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable int id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get bookings by user ID")
    public ResponseEntity<List<BookingDto>> getAllBookingsByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByUserId(userId));
    }

    @GetMapping("/computer/{computerId}")
    @Operation(summary = "Get bookings by computer ID")
    public ResponseEntity<List<BookingDto>> getAllBookingsByComputerId(@PathVariable int computerId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByComputerId(computerId));
    }

    @GetMapping("/person")
    @Operation(summary = "Get bookings by person name")
    public ResponseEntity<List<BookingDto>> getAllBookingsByPersonName(@RequestParam String name) {
        return ResponseEntity.ok(bookingService.getAllBookingsByPersonName(name));
    }

    @PostMapping("/add")
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingUpdateDto booking) {
        BookingDto saved = bookingService.addBooking(booking);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking by ID")
    public ResponseEntity<BookingDto> updateBooking(@Valid @RequestBody BookingUpdateDto dto, @PathVariable int id) {
        return ResponseEntity.ok(bookingService.updateBooking(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete booking by ID")
    public ResponseEntity<Void> deleteBooking(@PathVariable int id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current")
    @Operation(summary = "Get bookings of the current authenticated user")
    public ResponseEntity<List<BookingDto>> getCurrentUserBookings() {
        List<BookingDto> bookings = bookingService.getCurrentUserBookings();
        return ResponseEntity.ok(bookings);
    }
}
