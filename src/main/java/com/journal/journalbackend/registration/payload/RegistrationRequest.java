package com.journal.journalbackend.registration.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequest {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

}
