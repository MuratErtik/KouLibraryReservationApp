package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {

    @NotNull(message = "User status cannot be null")
    private UserStatus status;
}