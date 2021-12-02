package order;

import dto.DtoOrder;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.notNullValue;
import static utils.Configuration.BASE_URL;

@RunWith(Parameterized.class)
public class CreateOrderColorParameterizedTest {

    private final String colors;

    public CreateOrderColorParameterizedTest(String colors) {
        this.colors = colors;
    }

    public String[] fromStringToArray(String colors) {
        return colors.split(",");
    }

    @Parameterized.Parameters(name = "Заказ самоката цвета: {0}")
    public static Object[] getColor() {
        return new Object[][] {
                {"BLACK"},
                {"GREY"},
                {"BLACK,GREY"}
        };
    }

    @Test
    public void checkCreateOrderWithDiffColors() {

        DtoOrder request = new DtoOrder(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                fromStringToArray(colors)
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
