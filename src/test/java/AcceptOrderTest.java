import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static utils.scooterRegisterCourier.registerNewCourierAndReturnLoginPassword;
import static utils.supportClass.*;

public class AcceptOrderTest {

    @Test
    @DisplayName("Успешное принятие заказа")
    public void checkAcceptOrderSuccess() {

        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();
        int id = getCourierId(courier.get(0), courier.get(1));

        int track = createOrder();
        int idOrder = getOrderByTrack(track);

        Response response = given()
                .pathParam("id", idOrder)
                .param("courierId", id)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/{id}");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOk.json"))
                .body("ok", equalTo(true));

        deleteCourier(id);
    }

    @Test
    @DisplayName("Ошибка принятия заказа. В запросе отсутствует path-параметр id")
    @Description("Баг. В ответе возвращается код 404 Not Found вместо 400 Bad Request")
    public void checkAcceptOrderWithoutIdError() {

        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();
        int id = getCourierId(courier.get(0), courier.get(1));

        Response response = given()
                .param("courierId", id)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для поиска"));

        deleteCourier(id);
    }

    @Test
    @DisplayName("Ошибка принятия заказа. В запросе отсутствует параметр courierId")
    public void checkAcceptOrderWithoutCourierIdError() {

        int track = createOrder();
        int idOrder = getOrderByTrack(track);

        Response response = given()
                .pathParam("id", idOrder)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/{id}");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка принятия заказа. Некорректный id")
    public void checkAcceptOrderWrongIdError() {

        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();
        int id = getCourierId(courier.get(0), courier.get(1));

        int idOrder = 1000000;

        Response response = given()
                .pathParam("id", idOrder)
                .param("courierId", id)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/{id}");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Заказа с таким id не существует"));

        deleteCourier(id);
    }

    @Test
    @DisplayName("Ошибка принятия заказа. Некорректный courierId")
    public void checkAcceptOrderWrongCourierIdError() {

        int id = 1;

        int track = createOrder();
        int idOrder = getOrderByTrack(track);

        Response response = given()
                .pathParam("id", idOrder)
                .param("courierId", id)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/{id}");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Ошибка принятия заказа. Заказ уже был в работе")
    public void checkAcceptOrderRepeatAcceptError() {

        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();
        int id = getCourierId(courier.get(0), courier.get(1));

        int track = createOrder();
        int idOrder = getOrderByTrack(track);

        acceptOrder(idOrder, id);

        Response response = given()
                .pathParam("id", idOrder)
                .param("courierId", id)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/{id}");

        response.then()
                .assertThat()
                .statusCode(409)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Этот заказ уже в работе"));

        deleteCourier(id);
    }
}
