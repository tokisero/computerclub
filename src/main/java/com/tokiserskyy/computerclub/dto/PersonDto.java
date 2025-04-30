    package com.tokiserskyy.computerclub.dto;

    import com.fasterxml.jackson.annotation.JsonInclude;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.util.List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class PersonDto {
        private int id;
        private String name;

        @NotBlank(message = "Username cannot be empty")
        private String username;

        @Email(message = "Email is incorrect")
        @NotBlank(message = "Email is required")
        private String email;

        @Size(min = 8, message = "Password must be 8 or more characters long")
        @NotBlank(message = "Password is null")
        private String password;
        private List<BookingDto> bookings;
    }
