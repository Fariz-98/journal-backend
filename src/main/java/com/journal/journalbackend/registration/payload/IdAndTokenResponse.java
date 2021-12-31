package com.journal.journalbackend.registration.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdAndTokenResponse {

    private Long id;
    private String token;

}
