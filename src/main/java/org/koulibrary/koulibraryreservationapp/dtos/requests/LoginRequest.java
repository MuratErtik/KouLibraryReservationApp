package org.koulibrary.koulibraryreservationapp.dtos.requests;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Student ID number cannot be blank")
    @Pattern(regexp = "^\\d{9}$", message = "Student ID must be exactly 9 digits")
    private String studentIdNumber;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    private String password;
}
