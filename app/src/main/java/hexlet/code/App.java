package hexlet.code;

import hexlet.code.controller.UrlController;
import hexlet.code.controller.RootController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public final class App {

    private static final String DEFAULT_PORT = "8080";

    public static void main(String[] args) {
        getApp().start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });

        app.get("/", RootController.welcome());
        app.get("/urls", UrlController.listUrls());
        app.get("/urls/{id}", UrlController.showUrl());
        app.post("/urls", UrlController.newUrl());

        app.before(ctx -> ctx.attribute("ctx", ctx));

        return app;
    }

//    private static void addRoutes(Javalin app) {
//        app.get("/", RootController.welcome);
//        app.get("/about", RootController.about);
//
//        app.routes(() -> {
//            path("articles", () -> {
//                get(ArticleController.listArticles);
//                post(ArticleController.createArticle);
//                get("new", ArticleController.newArticle);
//                path("{id}", () -> {
//                    get(ArticleController.showArticle);
//                });
//            });
//        });
//    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.parseInt(port);
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
//        templateResolver.setCacheable(false);

        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }
}
