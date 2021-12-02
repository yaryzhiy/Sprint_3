package courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static utils.Configuration.BASE_URL;
import static utils.ScooterRegisterCourier.registerNewCourierAndReturnLoginPassword;
import static utils.CourierMethods.getCourierId;

public class DeleteCourierTest {

    @Test
    @DisplayName("Успешное удаление курьера")
    public void checkDeleteCourierSuccess() {

        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();
        int id = getCourierId(courier.get(0), courier.get(1));

        Response response = given()
                .pathParam("id", id)
                .when()
                .delete(BASE_URL + "/api/v1/courier/{id}");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOk.json"))
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка удаления курьера. В запросе отсутствует path-параметр id")
    @Description("Баг. В ответе возвращается код 404 Not Found вместо 400 Bad Request. Также возвращается неверное сообщение")
    public void checkDeleteCourierWithoutIdError() {

        Response response = given()
                .when()
                .delete(BASE_URL + "/api/v1/courier/");

        response.then()
                .assertThat()
                .log().body()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Ошибка удаления курьера. Несуществующий id")
    @Description("Баг. Неверное сообщение при ошибке (лишняя точка)")
    public void checkDeleteCourierWrongIdError() {

        int id = 1;

        Response response = given()
                .pathParam("id", id)
                .when()
                .delete(BASE_URL + "/api/v1/courier/{id}");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Курьера с таким id нет"));
    }
}
