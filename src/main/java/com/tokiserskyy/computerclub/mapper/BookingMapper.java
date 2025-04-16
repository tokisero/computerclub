package com.tokiserskyy.computerclub.mapper;

import lombok.experimental.UtilityClass;
import com.tokiserskyy.computerclub.dto.BookingDto;
import com.tokiserskyy.computerclub.model.Booking;

@UtilityClass
public class BookingMapper {

    public BookingDto toDtoShallow(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        return dto;
    }

    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = toDtoShallow(booking);
        dto.setComputer(ComputerMapper.toDtoWithoutBookings(booking.getComputer()));
        dto.setPerson(PersonMapper.toDtoShallow(booking.getPerson()));
        return dto;
    }

    public BookingDto toDtoWithoutPersons(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = toDtoShallow(booking);
        dto.setComputer(ComputerMapper.toDtoWithoutBookings(booking.getComputer()));
        return dto;
    }

    public BookingDto toDtoWithoutComputers(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = toDtoShallow(booking);
        dto.setPerson(PersonMapper.toDtoShallow(booking.getPerson()));
        return dto;
    }

    public Booking toEntity(BookingDto dto) {
        if (dto == null) return null;
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setComputer(ComputerMapper.toEntity(dto.getComputer()));
        booking.setPerson(PersonMapper.toEntity(dto.getPerson()));
        return booking;
    }
}
