import dto.DtoOrder;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.notNullValue;
import static utils.supportClass.BASE_URL;

public class CreateOrderColorTest {

    @Test
    @DisplayName("Заказ самоката цвета: []")
    public void checkCreateOrderWithEmptyColor() {

        String[] colors = {};

        DtoOrder request = new DtoOrder(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("jsonSchTrack.json"))
                .body("track", notNullValue());
    }

    @Test
    @DisplayName("Заказ самоката цвета: [null]")
    public void checkCreateOrderWithNullColor() {

        String[] colors = {null};

        DtoOrder request = new DtoOrder(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("jsonSchTrack.json"))
                .body("track", notNullValue());
    }

    @Test
    @DisplayName("Заказ самоката без выбора цвета")
    public void checkCreateOrderWithoutColor() {

        DtoOrder request = new DtoOrder(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha"
        );

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("jsonSchTrack.json"))
                .body("track", notNullValue());
    }
}
