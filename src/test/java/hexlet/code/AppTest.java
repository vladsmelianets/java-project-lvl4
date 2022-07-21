package hexlet.code;

import io.javalin.Javalin;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void getAppTest() {
        Assertions.assertThat(App.getApp()).isInstanceOf(Javalin.class);
    }
}
