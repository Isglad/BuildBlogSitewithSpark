package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash-message";

    public static void main(String[] args) {

        // Adding static file location
        staticFileLocation("/public");

        BlogDao blogDao = new SimpleBlogDao();

        // Checking if cookie exist for every single request
        before((req, res) -> {
            // check if cookie exists
            if(req.cookie("password") != null) {
                // if it exists, add attribute to the request
                req.attribute("password", req.cookie("password"));
            }
        });

        /* Implementing a before filter to check for admin access
            This filter will protect the /entries/new and /entries/:slug/edit routes.
            It will ensure that only authorized users can access those page.
            Authorized user must have admin as password
        */

        before("/new", (req, res) -> {
            if(req.attribute("password") == null || !req.attribute("password").equals("admin")) {
                // send message about redirect
                setFlashMessage(req, "Access denied! Please sign in to continue.");
                res.redirect("/password");
                //  let's stop the request from hitting one of the other routes
                halt();
            }

        });

        before("/entries/:slug/edit", (req, res) -> {
            if(req.attribute("password") == null || !req.attribute("password").equals("admin")) {
                // send message about redirect
                setFlashMessage(req, "Access denied! Please sign in to continue.");
                res.redirect("/password");
                //  let's stop the request from hitting one of the other routes
                halt();
            }
        });

        /*Home page
            Purpose: displays all entries
            HTTP Method: GET
            Linked DAO Method: findAllEntries()
            Template: index.hbs
         */
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", blogDao.findAllEntries());
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        /*Detail page
            * Purpose: Shows the details of a specif blog entry, including its comments
            * HTTP Method: GET
            * Linked DAO Method: findEntryBySlug()
            * Template:details.hbs
        */
        get("/entries/:slug", (req,res) -> {
            String slug = req.params(":slug");
            // Retrieve the slug from the URL and look up the corresponding blog entry
            BlogEntry entry = blogDao.findEntryBySlug(slug);
            Map<String, Object> model = new HashMap<>();
            model.put("entry", entry);
            // Pass the blog entry, its comments, and the slug to the template for rendering
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        /*Add new entry page
             * Purpose: displays a form to add new blog entry
             * HTTP Method: GET
             * Template:new.hbs
         */
        get("/new", (req,res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Create New Blog Entry");
            return new ModelAndView(model, "new.hbs");
        }, new HandlebarsTemplateEngine());

        /*Adding a new blog entry to the home page
             * Purpose: adds a new blog entry to the system
             * HTTP Method: POST
             * Linked DAO Method: addEntry(BlogEntry blogEntry)
         */
        post("/entries", (req, res) -> {
            String title = req.queryParams("title");
            String content = req.queryParams("content");
            BlogEntry entry = new BlogEntry(title, content);
            if(blogDao.addEntry(entry)) {
                setFlashMessage(req, "Thanks! Your blog has been added!");
            } else {
                setFlashMessage(req, "Whoops! Failed to add your blog");
            }
            res.redirect("/");
            return null;
        });

        /*Edit page
             * Purpose: displays a form to edit an existing blog entry
             * HTTP Method: GET
             * Linked DAO Method: findEntryBySlug()
             * Template:edit.hbs
         */
        get("/entries/:slug/edit", (req, res) -> {
            String slug = req.params("slug");
            BlogEntry entry = blogDao.findEntryBySlug(slug);
            Map<String, Object> model = new HashMap<>();
            model.put("entry", entry);
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        /*Adding an edited version to blog page
             * Purpose: adds an edited blog back to the system
             * HTTP Method: POST
             * Linked DAO Method: findEntryBySlug()
         */
        post("/entries/:slug", (req, res) -> { // I need to understand why we are not posting "/entries/:slug/edit" instead
            String slug = req.params("slug");
            // Fetch the blog entry by slug
            BlogEntry entry = blogDao.findEntryBySlug(slug);
            // Storing changes
            String newTitle = req.queryParams("title");
            String newContent = req.queryParams("content");
            String tagsInput = req.queryParams("tags");

            // Check if tagsInput is empty or null, and if so, set tags to an empty Set
            Set<String> tags = new HashSet<>();
            if(tagsInput != null && !tagsInput.trim().isEmpty()) {
                String[] tagsArray = tagsInput.split(",");
                for (String tag : tagsArray) {
                    tags.add(tag.trim());
                }
            };

            // Updating the Blog Entry with new title and new content
            entry.setTitle(newTitle);
            entry.setContent(newContent);
            entry.setTags(tags);
            // Redirecting to the updated slug
            res.redirect("/entries/" + entry.getSlug());
            return null;
        });

        /* Comment page
             * Purpose: allows users to post comments on a specific blog entry
             * HTTP Method: POST
             * Linked DAO Method: None (modifies the BlogEntry model directly)
         */
        post("/entries/:slug/comments", (req, res) -> {
            String slug = req.params(":slug");
            // Retrieve the slug from the URL and look up the corresponding blog entry
            BlogEntry entry = blogDao.findEntryBySlug(slug);
            // Extract the commenter's name from the request
            String author = req.queryParams("name");
            // Extract the comment content from the request
            String content = req.queryParams("comment");
            Comment comment = new Comment(author, content);
            // Adding the comment to the blog entry's comment list
            entry.addComment(comment);
            // Redirect back to the same detail page
            res.redirect("/entries/" + slug);
            return null;
        });

        /* Password page
             * Purpose: displays the password page when user access /password
             * HTTP Method: GET
            * Template:password.hbs
         */
        get("/password", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());


        /* Handle Password Form submission
            * if the username is "admin", set a cookie with the key "password" and value "admin"
            * Redirect the user to "/entries/new" which is the new entry page
            * if the username doesn't match, return an error message and redirect back to the home page
        */
        post("/password", (req, res) -> {
            String password = req.queryParams("password");
            if (password.equals("admin")) {
                res.cookie("password", password);
                setFlashMessage(req, "Successfully signed in!.");
                res.redirect("/");
            } else {
                setFlashMessage(req, "Wrong password. Please try again.");
                res.redirect("/password");
            }
            return null;
        });

        /* Delete Blog Entry
             * Purpose: allows the user to delete a blog entry and redirect back to home page
             * HTTP Method: DELETE
             * Linked DAO Method: deleteEntryBySlug
         */
        post("/entries/:slug/delete", (req,res) -> {
            // Check if the "_method" parameter is set to DELETE
            if ("DELETE".equals(req.queryParams("_method"))){
                String slug = req.params(":slug");
                // Call DAO to delete the entry
                blogDao.deleteEntryBySlug(slug);
                setFlashMessage(req, "Blog entry deleted successfully.");
                // Redirect to entries page
                res.redirect("/");
            } else {
                setFlashMessage(req, "Whoops! Failed to delete your blog");
            }
            return null;
        });

        /*    TESTING SECTION      */

        // Example blogs with no tags
        BlogEntry entry1 = new BlogEntry("The best day I’ve ever had", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien.");
        BlogEntry entry2 = new BlogEntry("The absolute worst day I’ve ever had", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien.");
        BlogEntry entry3 = new BlogEntry("That time at the mall", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien.");
        blogDao.addEntry(entry1);
        blogDao.addEntry(entry2);
        blogDao.addEntry(entry3);

        // Example comment
        Comment entry1Comment = new Comment("Carling Kirk", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.");
        entry1.addComment(entry1Comment);

        // Example of a new blogEntry that takes multiple tags
        BlogEntry entryWithTags = new BlogEntry("Blog Post with tags", "This is the content", "Blog", "Java", "Spark");
        blogDao.addEntry(entryWithTags);

        // Example adding tags to an existing entry
        entry2.addTag("HTTP");

    }

    /*
    Using flash message to keep user informed about their actions
    */

    private static void setFlashMessage(Request req, String message) {
        // request has session method
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        // passing "false" as a parameter to req.session, means we're not creating one as it is
        // expensive and wasteful if we don't want one.
        if (req.session(false) == null) {
            return null;
        }
        // if the req.session doesn't contain a message, get out or return null!
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        // if it does contain a message,pop it out. What happens here,
        // we need to cast the req.session value which is an object to a String
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    // Method that will make a message disappear once the user saw it
    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        // if the message isn't empty means we have a session, then let's remove the session key
        if(message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

}
