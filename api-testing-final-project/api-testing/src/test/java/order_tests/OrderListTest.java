package order_tests;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        File json = new File("src/main/resources/courierlogin/courierLogin.json");

        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");

        courierId = given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .path("id");
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            given()
                    .pathParam("id", courierId)
                    .delete("/api/v1/courier/{id}");
        }
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что успешный запрос возвращает список заказов в теле ответа")
    public void orderListReturnsInResponseBody() {
        given()
                .queryParam("courierId", courierId)
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
