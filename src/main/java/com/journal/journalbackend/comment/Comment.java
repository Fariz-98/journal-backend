package com.journal.journalbackend.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.journal.journalbackend.journal.Journal;
import com.journal.journalbackend.user.AppUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    private String content;
    private LocalDateTime dateCreated;

    @JsonIgnore
    @JoinColumn
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private AppUser appUser;

    @JsonIgnore
    @JoinColumn
    @ManyToOne(cascade = CascadeType.REFRESH)
    private Journal journal;

    public Comment(String author, String content, LocalDateTime dateCreated, Journal journal, AppUser appUser) {
        this.author = author;
        this.content = content;
        this.dateCreated = dateCreated;
        this.journal = journal;
        this.appUser = appUser;
    }

    public Comment(Long id, String author, String content, LocalDateTime dateCreated) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(author, comment.author) && Objects.equals(content, comment.content) && Objects.equals(dateCreated, comment.dateCreated) && Objects.equals(appUser, comment.appUser) && Objects.equals(journal, comment.journal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, content, dateCreated, appUser, journal);
    }
}
