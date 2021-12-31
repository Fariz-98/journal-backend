package com.journal.journalbackend.user.payload;

import com.journal.journalbackend.journal.Journal;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AppUserProfileResponse {

    private String username;
    private String firstName;
    private String lastName;
    private List<Journal> userJournal;

}
