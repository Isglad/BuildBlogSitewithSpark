package com.teamtreehouse.blog;

import static spark.Spark.staticFileLocation;

public class Main {
    public static void main(String[] args) {
        // Adding static file location
        staticFileLocation("/public");
    }
}
