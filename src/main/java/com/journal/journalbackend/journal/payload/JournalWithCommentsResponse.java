package com.journal.journalbackend.journal.payload;

import com.journal.journalbackend.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalWithCommentsResponse {

    private Long id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime dateCreated;
    private List<Comment> comments;

}
