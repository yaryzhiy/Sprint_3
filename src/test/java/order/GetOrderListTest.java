package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.*;
import static utils.Configuration.BASE_URL;
import static utils.OrderMethods.*;
import static utils.ScooterRegisterCourier.registerNewCourierAndReturnLoginPassword;
import static utils.CourierMethods.*;

public class GetOrderListTest {

    private static int id;

    @Before
    @DisplayName("Создание курьера и получение его id")
    public void setUp() {
        ArrayList<String> courier = registerNewCourierAndReturnLoginPassword();
        id = getCourierId(courier.get(0), courier.get(1));
    }

    @Test
    @DisplayName("Получение списка заказов со всеми параметрами")
    public void checkGetOrderListWithAllParams() {

        int track = createOrder();
        int idOrder = getOrderByTrack(track);
        acceptOrder(idOrder, id);

        String[] stations = {"1", "2"};
        Response response = given()
                .param("courierId", id)
                .param("nearestStation", (Object) stations)
                .param("limit", 5)
                .param("page", 0)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders[0].id", equalTo(idOrder))
                .body("orders[0].courierId", equalTo(id))
                .body("orders.size()", not(0));
    }

    @Test
    @DisplayName("Получение списка заказов без параметра courierId")
    public void checkGetOrderListWithoutParamCourierId() {

        String[] stations = {"1", "2"};
        Response response = given()
                .param("nearestStation", (Object) stations)
                .param("limit", 5)
                .param("page", 0)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders[0].courierId", equalTo(null))
                .body("orders.size()", not(0));
    }

    @Test
    @DisplayName("Получение списка заказов без параметра nearestStation")
    public void checkGetOrderListWithoutParamNearestStation() {

        int track = createOrder();
        int idOrder = getOrderByTrack(track);
        acceptOrder(idOrder, id);

        Response response = given()
                .param("courierId", id)
                .param("limit", 5)
                .param("page", 0)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders[0].id", equalTo(idOrder))
                .body("orders[0].courierId", equalTo(id))
                .body("orders.size()", not(0));
    }

    @Test
    @DisplayName("Получение списка заказов без параметра limit")
    public void checkGetOrderListWithoutParamLimit() {

        int track = createOrder();
        int idOrder = getOrderByTrack(track);
        acceptOrder(idOrder, id);

        String[] stations = {"1", "2"};
        Response response = given()
                .param("courierId", id)
                .param("nearestStation", (Object) stations)
                .param("page", 0)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders[0].id", equalTo(idOrder))
                .body("orders[0].courierId", equalTo(id))
                .body("orders.size()", not(0));
    }

    @Test
    @DisplayName("Получение списка заказов без параметра page")
    public void checkGetOrderListWithoutParamPage() {

        int track = createOrder();
        int idOrder = getOrderByTrack(track);
        acceptOrder(idOrder, id);

        String[] stations = {"1", "2"};
        Response response = given()
                .param("courierId", id)
                .param("nearestStation", (Object) stations)
                .param("limit", 5)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders[0].id", equalTo(idOrder))
                .body("orders[0].courierId", equalTo(id))
                .body("orders.size()", not(0));
    }

    @Test
    @DisplayName("Получение списка заказов без параметров")
    public void checkGetOrderListWithoutParams() {

        Response response = given()
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders[0].courierId", equalTo(null))
                .body("orders.size()", not(0));
    }

    @Test
    @DisplayName("Получение списка заказов курьера, у которого нет принятых заказов")
    public void checkGetOrderListOfCourierWithoutOrders() {

        String[] stations = {"1", "2"};
        Response response = given()
                .param("courierId", id)
                .param("nearestStation", (Object) stations)
                .param("limit", 5)
                .param("page", 0)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("jsonSchOrderData.json"))
                .body("orders.size()", is(0));
    }

    @Test
    @DisplayName("Ошибка получения списка заказов. Несуществующий id курьера")
    public void checkGetOrderListWithWrongCourierIdError() {

        int courierId = 1;
        String[] stations = {"1", "2"};
        Response response = given()
                .param("courierId", courierId)
                .param("nearestStation", (Object) stations)
                .param("limit", 5)
                .param("page", 0)
                .when()
                .get(BASE_URL + "/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(404)
                .body(matchesJsonSchemaInClasspath("jsonSchError.json"))
                .body("message", equalTo("Курьер с идентификатором " + courierId + " не найден"));
    }

    @After
    public void tearDown() {
        deleteCourier(id);
    }
}
