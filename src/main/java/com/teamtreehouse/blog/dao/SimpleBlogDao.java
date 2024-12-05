package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;

import java.util.ArrayList;
import java.util.List;

public class SimpleBlogDao implements BlogDao{
    private List<BlogEntry> entries;

    public SimpleBlogDao() {
        this.entries = new ArrayList<>();
    }

    // Implement addEntry method to store new blog entries
    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return entries.add(blogEntry);
    }

    // Develop findAllEntries method to retrieve all blog posts
    @Override
    public List<BlogEntry> findAllEntries() {
        return new ArrayList<>(entries);
    }

    // Implement findEntryBySlug to fetch a specific blog entry
    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return entries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }


    /*Extra Credit
        * Implement deleteEntryBySlug
    */
    @Override
    public void deleteEntryBySlug(String slug) {
        entries.removeIf(entry -> entry.getSlug().equals(slug));
    }
}
