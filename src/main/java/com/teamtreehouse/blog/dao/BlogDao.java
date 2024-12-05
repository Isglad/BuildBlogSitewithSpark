package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;

import java.util.List;

public interface BlogDao {
    // Add entry
    boolean addEntry(BlogEntry blogEntry);

    // Implementation of a way to find all entries
    List<BlogEntry> findAllEntries();

    // Adds the ability to find one by slug
    BlogEntry findEntryBySlug(String slug);

    /*Extra Credit
        * Adds the ability to delete one by slug
    */
    void deleteEntryBySlug(String slug);
}
