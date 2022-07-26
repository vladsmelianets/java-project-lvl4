package hexlet.code;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;

public final class App {

    public static void main(String[] args) {
        getApp().start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(JavalinConfig::enableDevLogging);
        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port != null ? Integer.parseInt(port) : 8080;
    }
}
