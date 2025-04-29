package com.tokiserskyy.computerclub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tokiserskyy.computerclub.cache.InMemoryCache;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputerDto implements InMemoryCache.HasId {
    private int id;

    @NotBlank(message = "CPU must not be blank")
    private String cpu;

    @NotNull(message = "RAM is required")
    @Min(value = 4, message = "RAM must be at least 4 GB")
    private Integer ram;

    @NotBlank(message = "GPU must not be blank")
    private String gpu;

    @NotNull(message = "Monitor size is required")
    @Min(value = 60, message = "Monitor size must be at least 60hz")
    private Integer monitor;

    private List<BookingDto> bookings;
    private List<GameDto> games;
}