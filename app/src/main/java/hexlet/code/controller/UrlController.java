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
            String urlName = ctx.formParam("url");

            URL parsed = null;
            try {
                parsed = new URL(urlName);
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.render("index.html");
            }

            Url urlToCreate = new Url(parsed.getPath() + ":" + parsed.getPort());
            boolean urlExists = new QUrl()
                    .name.equalTo(urlToCreate.getName()).exists();
            if (urlExists) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flash-type", "info");
            } else {
                urlToCreate.save();
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
