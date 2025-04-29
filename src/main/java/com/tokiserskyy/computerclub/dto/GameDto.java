package com.tokiserskyy.computerclub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameDto {
    private int id;

    @NotBlank(message = "Game name cannot be empty")
    private String name;

    @NotBlank(message = "Genre cannot be empty")
    private String genre;

    private List<ComputerDto> computers;
}
