package order_tests;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class OrderAcceptTest {
    private Integer courierId;
    private Integer orderId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        File courierJson = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(courierJson)
                .post("api/v1/courier");

        courierId = given()
                .header("Content-Type", "application/json")
                .body(courierJson)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .path("id");

        File orderJson = new File("src/main/resources/orders/createOrder.json");
        Integer orderTrack = given()
                .header("Content-Type", "application/json")
                .body(orderJson)
                .post("api/v1/orders")
                .then()
                .extract()
                .path("track");

        orderId = given()
                .queryParam("t", orderTrack)
                .get("api/v1/orders/track")
                .then()
                .extract()
                .path("order.id");
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
    @DisplayName("Успешное принятие заказа")
    @Description("Успешный запрос возвращает статус 200 и ok: true")
    public void successOrderAccept() {
        given()
                .pathParam("id", orderId)
                .queryParam("courierId", courierId)
                .put("api/v1/orders/accept/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Принятие заказа без id курьера")
    @Description("Если не передать id курьера, запрос вернёт ошибку 400")
    public void acceptOrderWithoutCourierId() {
        given()
                .pathParam("id", orderId)
                .queryParam("courierId", "")
                .put("api/v1/orders/accept/{id}")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с неверным id курьера")
    @Description("Если передать неверный id курьера, запрос вернёт ошибку 404")
    public void acceptOrderWithIncorrectCourierId() {
        given()
                .pathParam("id", orderId)
                .queryParam("courierId", "000000")
                .put("api/v1/orders/accept/{id}")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа без номера заказа")
    @Description("Если не передать номер заказа в пути, запрос вернёт ошибку 400")
    public void acceptOrderWithoutOrderId() {
        given()
                .pathParam("id", "")
                .queryParam("courierId", courierId)
                .put("api/v1/orders/accept/{id}")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с неверным номером заказа")
    @Description("Если передать неверный номер заказа, запрос вернёт ошибку 404")
    public void acceptOrderWithIncorrectOrderId() {
        given()
                .pathParam("id", "000000")
                .queryParam("courierId", courierId)
                .put("api/v1/orders/accept/{id}")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}

