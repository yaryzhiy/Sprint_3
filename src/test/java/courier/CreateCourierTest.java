package courier;

import dto.DtoCourier;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static utils.Configuration.BASE_URL;
import static utils.ScooterRegisterCourier.registerNewCourierAndReturnLoginPassword;
import static utils.CourierMethods.*;

public class CreateCourierTest {

    @Test
    @DisplayName("Успешное создание курьера. В теле запроса указаны все поля")
    public void checkCreateCourierSuccess() {

        DtoCourier request = new DtoCourier("saske", "1234", "Saske");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier");

        response.then()
                .assertThat()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("jsonSchOk.json"))
                .body("ok", equalTo(true));

        int id = getCourierId(request.login, request.password);
        deleteCourier(id);
    }

    @Test
    @DisplayName("Успешное создание курьера. В теле запроса указаны только обязательные поля")
    public void checkCreateCourierRequiredFieldsSuccess() {

        DtoCourier request = new DtoCourier();
        request.setLogin("saske");
        request.setPassword("1234");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier");

        response.then()
                .assertThat()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("jsonSchOk.json"))
                .body("ok", equalTo(true));

        int id = getCourierId(request.login, request.password);
        deleteCourier(id);
    }

    @Test
    @DisplayName("Ошибка при создании курьера. В теле запроса отвутствует поле 'login'")
    public void checkCreateCourierWithoutLoginError() {

        DtoCourier request = new DtoCourier();
        request.setPassword("1234");
        request.setFirstName("Saske");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при создании курьера. В теле запроса отвутствует поле 'password'")
    public void checkCreateCourierWithoutPasswordError() {

        DtoCourier request = new DtoCourier();
        request.setLogin("saske");
        request.setFirstName("Saske");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка при создании курьера. Нельзя создать курьера с одинаковым логином")
    @Description("Баг. Неверное сообщение в ответе")
    public void checkCreateCourierEqualLoginError() {

        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();

        DtoCourier request = new DtoCourier();
        request.setLogin(courier.get(0));
        request.setPassword(courier.get(1));

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier");

        response.then()
                .assertThat()
                .statusCode(409)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Этот логин уже используется"));
    }

}
