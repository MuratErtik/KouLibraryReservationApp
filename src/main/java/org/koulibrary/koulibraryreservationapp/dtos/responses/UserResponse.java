package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.UserRole;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String studentIdNumber;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole userRole;
    private UserStatus userStatus;
}