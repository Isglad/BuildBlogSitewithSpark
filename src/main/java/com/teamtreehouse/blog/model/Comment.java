package com.teamtreehouse.blog.model;


import java.time.LocalDateTime;

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

    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
