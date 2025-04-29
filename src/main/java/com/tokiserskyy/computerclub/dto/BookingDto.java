package com.tokiserskyy.computerclub.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private int id;

    @NotBlank(message = "Start time cannot be empty")
    private LocalDateTime startTime;

    @NotBlank(message = "End time cannot be empty")
    private LocalDateTime endTime;

    private ComputerDto computer;
    private PersonDto person;
}




