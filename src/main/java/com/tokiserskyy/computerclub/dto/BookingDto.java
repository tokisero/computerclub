package com.tokiserskyy.computerclub.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ComputerDto computer;
    private PersonDto person;
}




