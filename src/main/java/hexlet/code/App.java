package hexlet.code;

import io.javalin.Javalin;

public final class App {

    public static Javalin getApp() {
        return Javalin.createStandalone();
    }

    public static void main(String[] args) {
        getApp().start();
    }
}
