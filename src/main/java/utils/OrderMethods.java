package utils;

import dto.DtoOrder;
import io.qameta.allure.Description;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static utils.Configuration.BASE_URL;

public class OrderMethods {

    @Step("Создание заказа")
    public static int createOrder() {

        String[] colors = {"BLACK"};
        DtoOrder request = new DtoOrder(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "2",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );

        int track = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .when()
                .post(BASE_URL + "/api/v1/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("track");

        return track;
    }

    @Step("Получение заказа по его номеру")
    public static int getOrderByTrack(int track) {

        int id = given()
                .param("t", track)
                .when()
                .get(BASE_URL + "/api/v1/orders/track")
                .then()
                .statusCode(200)
                .extract()
                .path("order.id");

        return id;
    }

    @Step("Принятие заказа курьером")
    @Description("Баг. Заказ дублируется при привязке его к курьеру")
    public static void acceptOrder(int id, int courierId) {

        given()
                .pathParam("id", id)
                .param("courierId", courierId)
                .when()
                .put(BASE_URL + "/api/v1/orders/accept/{id}")
                .then()
                .statusCode(200);
    }
}
