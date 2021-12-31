package com.journal.journalbackend.journal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.journal.journalbackend.comment.Comment;
import com.journal.journalbackend.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime dateCreated;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "journal")
    private List<Comment> comment;

    @JsonIgnore
    @JoinColumn
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private AppUser appUser;

    public Journal(String author, String title, String content, LocalDateTime dateCreated, AppUser appUser) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
        this.appUser = appUser;
    }

    public Journal(Long id, String author, String title, String content, LocalDateTime dateCreated) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journal journal = (Journal) o;
        return Objects.equals(id, journal.id) && Objects.equals(author, journal.author) && Objects.equals(title, journal.title) && Objects.equals(content, journal.content) && Objects.equals(dateCreated, journal.dateCreated) && Objects.equals(comment, journal.comment) && Objects.equals(appUser, journal.appUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, title, content, dateCreated, comment, appUser);
    }
}
