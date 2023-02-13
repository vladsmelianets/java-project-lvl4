package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static Javalin app;
    private static String baseUrl;
    private static Database database;

    private static MockWebServer mockServer;

    @BeforeAll
    public static void setUp() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();

        mockServer = new MockWebServer();
        String fixture = Files.readString(
                Paths.get("./build/resources/test/test-page-fixture.html"), StandardCharsets.UTF_8);
        MockResponse mockedResponse = new MockResponse().setBody(fixture);
        mockServer.enqueue(mockedResponse);
        mockServer.enqueue(mockedResponse);
        mockServer.start();
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

    //TODO add testShowUrl
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
        assertThat(response.getBody())
                .as("page should contain status code")
                .contains("200");
    }

    @Test
    void testNewUrl() {
        HttpUrl mockUrl = mockServer.url("");
        String inputUrlName = mockUrl + "somepath";
        String persistedName = mockUrl.toString().replaceAll("/$", "");

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

    @Test
    void testNewCheck() {
        String mockedUrlToCheck = mockServer.url("").toString().replaceAll("/$", "");
        Url url = new Url(mockedUrlToCheck);
        url.save();
        long id = url.getId();

        List<UrlCheck> checksBefore = new QUrlCheck()
                .url.id.equalTo(id)
                .findList();

        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls/" + id + "/checks")
                .asEmpty();

        List<UrlCheck> checksAfter = new QUrlCheck()
                .url.id.equalTo(id)
                .orderBy().createdAt.asc()
                .findList();
        UrlCheck latestCheck = checksAfter.get(0);

        assertThat(checksAfter.size()).as("new check should be persisted in db").isEqualTo(checksBefore.size() + 1);
        assertThat(latestCheck.getTitle()).as("title should be parsed correctly").isEqualTo("stab title");
        assertThat(latestCheck.getH1()).as("h1 should be parsed correctly").isEqualTo("stab h1");
        assertThat(latestCheck.getDescription()).as("description should be parsed correctly")
                .isEqualTo("stab description");

        assertThat(responsePost.getStatus()).as("should be redirected by post response").isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).as("should be redirected to /urls/{id}")
                .isEqualTo("/urls/" + id);

        HttpResponse<String> redirectedResponse = Unirest
                .get(baseUrl + "/urls/" + id)
                .asString();
        assertThat(redirectedResponse.getStatus()).as("should show url details page after check").isEqualTo(200);
    }
}
