package com.journal.journalbackend.comment.payload;

import com.journal.journalbackend.comment.Comment;
import com.journal.journalbackend.journal.Journal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentAndJournalResponse {

    private Journal journal;
    private Comment comment;

}
