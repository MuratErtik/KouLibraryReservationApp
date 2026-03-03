package org.koulibrary.koulibraryreservationapp.entities;


import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.envers.Audited;
import org.koulibrary.koulibraryreservationapp.domains.UserRole;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
// @Audited
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(nullable = false, unique = true)
    private String studentIdNumber;

    private String firstName;

    private String lastName;

    private String email;
}
