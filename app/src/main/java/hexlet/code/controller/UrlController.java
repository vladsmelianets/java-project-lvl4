package hexlet.code.controller;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import java.net.URL;

import java.util.List;

public final class UrlController {

    private UrlController() {
    }

    public static Handler newUrl() {
        return ctx -> {
            String name = ctx.formParam("url");
            
            try {
                URL parsed = new URL(name);
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.render("index.html");
            }

//            System.out.println("CREATING URL: " + url);
            Url url = new Url(parsed.getPath() + ":" + parsed.getPort());
            Url existing = new QUrl()
            .name.equalTo(url.getName())
            .findOne();

            if(existing != null) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "info");
            } else {
                url.save();
                ctx.sessionAttribute("flash", "Страница создана");
                ctx.sessionAttribute("flash-type", "success");
            }            
            ctx.redirect("/urls");
        };
    }

    public static Handler listUrls() {
        return ctx -> {
            List<Url> urls = new QUrl()
                    .orderBy()
                    .id.asc()
                    .findList();
            ctx.attribute("urls", urls);
            ctx.render("urls.html");
        };
    }

    public static Handler showUrl() {
        return ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
            Url url = new QUrl()
                    .id.equalTo(id)
                    .findOne();
            ctx.attribute("url", url);
            ctx.render("show.html");
        };
    }
}
