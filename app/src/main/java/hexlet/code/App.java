package hexlet.code;

import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class App {

    private static final String DEFAULT_PORT = "8080";

    public static void main(String[] args) {
        getApp().start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (!isProduction()) {
                config.enableDevLogging();
            }
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });
        addRoutes(app);
        app.before(ctx -> ctx.attribute("ctx", ctx));
        return app;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome());
        app.routes(() ->
                path("urls", () -> {
                    get(UrlController.listUrls());
                    post(UrlController.newUrl());
                    path("{id}", () -> {
                        get(UrlController.showUrl());
                        post("checks", UrlController.newCheck());
                    });
                }));
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.parseInt(port);
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "dev");
    }

    private static boolean isProduction() {
        return getMode().equals("prod");
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setCharacterEncoding("UTF-8");

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
//        templateResolver.setCacheable(false);

        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }
}
