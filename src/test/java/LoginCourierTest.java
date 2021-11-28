import dto.DtoCourier;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static utils.scooterRegisterCourier.registerNewCourierAndReturnLoginPassword;
import static utils.supportClass.*;

public class LoginCourierTest {

    private static ArrayList<String> courier;

    @BeforeClass
    @DisplayName("Создание курьера")
    public static void setUp() {
        courier = registerNewCourierAndReturnLoginPassword();
    }

    @Test
    @DisplayName("Успешная авторизация")
    public void checkLoginCourierSuccess() {

        DtoCourier request = new DtoCourier();
        request.setLogin(courier.get(0));
        request.setPassword(courier.get(1));

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchId.json"))
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка авторизации. Неверный логин")
    public void checkLoginCourierWrongLoginError() {

        DtoCourier request = new DtoCourier();
        request.setLogin("login");
        request.setPassword(courier.get(1));

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка авторизации. Неверный пароль")
    public void checkLoginCourierWrongPasswordError() {

        DtoCourier request = new DtoCourier();
        request.setLogin(courier.get(0));
        request.setPassword("password");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка авторизации. В теле запроса отсутствует поле 'login'")
    public void checkLoginCourierWithoutLoginError() {

        DtoCourier request = new DtoCourier();
        request.setPassword(courier.get(1));

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка авторизации. В теле запроса отсутствует поле 'password'")
    @Description("Баг. Возвращается код 504 Gateway time out вместо 400 Bad Request")
    public void checkLoginCourierWithoutPasswordError() {

        DtoCourier request = new DtoCourier();
        request.setLogin(courier.get(0));

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

        response.then()
                .assertThat()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @AfterClass
    @DisplayName("Удаление курьера")
    public static void tearDown() {
        int id = getCourierId(courier.get(0), courier.get(1));
        deleteCourier(id);
    }
}
