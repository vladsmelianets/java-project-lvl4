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
            Url url = findById(id);
            ctx.attribute("url", url);
            ctx.render("show.html");
        };
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
                UrlCheck check = check(urlToCreate);
                urlToCreate.getUrlChecks().add(check);
                urlToCreate.save();
                ctx.sessionAttribute("flash", "Страница создана");
                ctx.sessionAttribute("flash-type", "success");
            }
            ctx.redirect("/urls");
        };
    }

    public static Handler newCheck() {
        return ctx -> {
            long urlId = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
            Url url = findById(urlId);
            url.getUrlChecks().add(check(url));
            //TODO concern: should we save UrlCheck instead? This will require Url setter in urlCheck.
            url.save();
            ctx.redirect("/urls/" + urlId);
        };
    }

    //TODO implement body parsing
    private static UrlCheck check(Url url) {
        return new UrlCheck(200, "stab title", "stab h1", "stab descr");
    }

    private static Url findById(long id) {
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();
        if (url == null) {
            throw new NotFoundResponse();
        }
        return url;
    }

    private static Url mapToUrl(URL url) {
        String urlName = url.getProtocol()
                + "://"
                + url.getHost()
                + (url.getPort() != -1 ? (":" + url.getPort()) : "");
        return new Url(urlName);
    }
}
