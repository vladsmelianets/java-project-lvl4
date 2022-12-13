package hexlet.code.controller;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;

import java.util.List;

public final class UrlController {

    private UrlController() {
    }

    public static Handler newUrl() {
        return ctx -> {
            String name = ctx.formParam("url");
            Url url = new Url(name);
//            System.out.println("CREATING URL: " + url);
            url.save();
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
