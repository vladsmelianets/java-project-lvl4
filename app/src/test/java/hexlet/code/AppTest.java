package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Database database;

    @BeforeAll
    public static void setUpCtx() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @BeforeEach
    void rollbackDatabase() {
        database.script().run("/schema-test.sql");
        database.script().run("/seed-test.sql");
    }

    @AfterAll
    public static void shutDown() {
        app.stop();
    }

    @Test
    void testIndex() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertThat(response.getStatus()).as("should return status 200").isEqualTo(200);
        assertThat(response.getBody()).as("page should contain correct body").contains("Анализатор страниц");
    }

    @Test
    void testListUrls() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
        assertThat(response.getStatus()).as("should return status 200").isEqualTo(200);
        assertThat(response.getBody())
                .as("page should contain existent sites")
                .contains("https://www.test-site-one.net");
    }

    @Test
    void testNewUrl() {
        String inputUrlName = "https://www.new-site.com";
        String persistedName = "https://www.new-site.com";

        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", inputUrlName)
                .asEmpty();
        assertThat(responsePost.getStatus()).as("should be redirected by post response").isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).as("should be redirected to /urls")
                .isEqualTo("/urls");

        Url createdUrl = new QUrl().name.equalTo(persistedName).findOne();
        assertThat(createdUrl).as("new url should be persisted in db").isNotNull();

        HttpResponse<String> redirectedResponse = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = redirectedResponse.getBody();
        assertThat(redirectedResponse.getStatus()).as("should show urls page after new url creation").isEqualTo(200);
        assertThat(body).as("urls page should contain flash msg upon new url creation").contains("Страница создана");
        assertThat(body).as("new url should be listed with correct name on urls page").contains(persistedName);
    }
}
