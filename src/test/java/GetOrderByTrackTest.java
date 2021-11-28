import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static utils.supportClass.BASE_URL;
import static utils.supportClass.createOrder;

public class GetOrderByTrackTest {

    @Test
    @DisplayName("Успешное получение заказа по его номеру")
    @Description("Баг. В ответе отсутствует атрибут courierFirstName")
    public void checkGetOrderByTrackSuccess() {

        int track = createOrder();

        Response response = given()
                .param("t", track)
                .when()
                .get(BASE_URL + "/api/v1/orders/track");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderByTrack.json"))
                .body("order.id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка получения заказа по его номеру. В запросе отсутствует параметр t")
    public void checkGetOrderByTrackWithoutTError() {

        Response response = given()
                .when()
                .get(BASE_URL + "/api/v1/orders/track");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка получения заказа по его номеру. Несуществующий t")
    public void checkGetOrderByTrackWrongTError() {

        int track = 1;

        Response response = given()
                .param("t", track)
                .when()
                .get(BASE_URL + "/api/v1/orders/track");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Заказ не найден"));
    }
}
