package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogEntry {
    // Adding necessary fields
    private String slug;
    private String title;
    private String content;
    private LocalDateTime creationDate;
    private List<Comment> comments;

    public BlogEntry(String title, String content) {
        this.title = title;
        this.content = content;
        this.creationDate = LocalDateTime.now();
        this.comments = new ArrayList<>();
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    // Implementation of a method that manages comments
    public boolean addComment(Comment comment) {
        // Store these comments!
        return comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getSlug() {
        return slug;
    }


    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
