package utils;

import dto.DtoCourier;
import dto.DtoOrder;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static utils.Configuration.BASE_URL;

public class CourierMethods {

    @Step("Получение id курьера")
    public static int getCourierId(String login, String password) {

        DtoCourier request = new DtoCourier();
        request.setLogin(login);
        request.setPassword(password);

        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        return id;
    }

    @Step("Удаление курьера")
    public static void deleteCourier(int id) {

        given()
                .header("Content-type", "application/json")
                .and()
                .pathParam("id", id)
                .when()
                .delete(BASE_URL + "/api/v1/courier/{id}")
                .then()
                .statusCode(200);
    }
}
