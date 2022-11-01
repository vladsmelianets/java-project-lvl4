package hexlet.code.controller;

import io.javalin.http.Handler;

public final class RootController {

    private RootController() {
    }

    public static Handler welcome() {
        return ctx -> ctx.render("index.html");
    }
}
