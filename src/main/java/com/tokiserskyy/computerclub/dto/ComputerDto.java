package com.tokiserskyy.computerclub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tokiserskyy.computerclub.model.Booking;
import com.tokiserskyy.computerclub.model.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputerDto {
    private int id;
    private int monitor;
    private String gpu;
    private String cpu;
    private int ram;
    private List<BookingDto> bookings;
    private List<GameDto> games;
}