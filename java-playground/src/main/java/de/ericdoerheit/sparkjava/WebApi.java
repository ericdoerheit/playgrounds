package de.ericdoerheit.sparkjava;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by ericdoerheit on 24/05/16.
 */
public class WebApi {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
        post("/hello", (req, res) -> {
            String body = req.body();
            return "Hello " + body;
        });
    }
}
