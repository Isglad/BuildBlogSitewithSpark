package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BlogEntry {
    // Adding necessary fields
    private String slug;
    private String title;
    private String content;
    private LocalDateTime creationDate;
    private List<Comment> comments;
    /* Extra Credit
        *Adding a "Tags" Property
    */
    private Set<String> tags;

    public BlogEntry(String title, String content, String... tags) {
        this.title = title;
        this.content = content;
        this.creationDate = LocalDateTime.now();
        this.comments = new ArrayList<>();
        //  Initializing the set with provided tags
        this.tags = new HashSet<>(Arrays.asList(tags));
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

    public String getFormattedDate () {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' H:mm");
        return creationDate.format(formatter);
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

    /* Extra Credit
        *Adding a "Tags" Property
    */
    public Set<String> getTags(){
        return tags;
    }

    public void addTag(String tag){
        this.tags.add(tag);
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

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
