package com.teamtreehouse.blog.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    // Adding fields
    private String author;
    private String content;
    private LocalDateTime creationDate;

    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
        this.creationDate = LocalDateTime.now();
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFormattedDate () {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' H:mm");
        return creationDate.format(formatter);
    }

    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
