package hexlet.code.controller;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;

import java.net.URL;
import java.util.List;
import java.util.Map;

public final class UrlController {

    private UrlController() {
    }

    public static Handler newUrl() {
        return ctx -> {
            String inputUrl = ctx.formParam("url");

            URL parsed;
            Url urlToCreate;
            try {
                parsed = new URL(inputUrl);
                urlToCreate = mapToUrl(parsed);
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.sessionAttribute("flash-type", "danger");
                ctx.redirect("/");
                return;
            }

            boolean isExist = new QUrl()
                    .name.equalTo(urlToCreate.getName())
                    .exists();
            if (isExist) {
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

            Map<Long, UrlCheck> urlChecks = new QUrlCheck()
                    .url.id.asMapKey()
                    .orderBy()
                    .createdAt.desc()
                    .findMap();

            ctx.attribute("urls", urls);
            ctx.attribute("urlChecks", urlChecks);
            ctx.render("urls.html");
        };
    }

    public static Handler showUrl() {
        return ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
            Url url = new QUrl()
                    .id.equalTo(id)
                    .findOne();
            if (url == null) {
                throw new NotFoundResponse();
            }
            ctx.attribute("url", url);
            ctx.render("show.html");
        };
    }

    private static Url mapToUrl(URL parsed) {
        String urlName = parsed.getProtocol()
                + "://"
                + parsed.getHost()
                + (parsed.getPort() != -1 ? (":" + parsed.getPort()) : "");
        return new Url(urlName);
    }
}
