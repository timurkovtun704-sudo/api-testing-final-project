package order_tests;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderGetByNumberTest {
    private Integer orderTrack;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        File orderJson = new File("src/main/resources/orders/createOrder.json");
        orderTrack = given()
                .header("Content-Type", "application/json")
                .body(orderJson)
                .post("api/v1/orders")
                .then()
                .extract()
                .path("track");
    }

    @Test
    @DisplayName("Успешное получение заказа по его номеру")
    @Description("Успешный запрос возвращает объект с заказом и статус 200")
    public void successGetOrderByNumber() {
        given()
                .queryParam("t", orderTrack)
                .get("api/v1/orders/track")
                .then()
                .assertThat()
                .statusCode(200)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Запрос без номера заказа")
    @Description("Запрос без номера заказа возвращает ошибку 400")
    public void getOrderWithoutNumber() {
        given()
                .queryParam("t", "")
                .get("api/v1/orders/track")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Запрос с несуществующим номером заказа")
    @Description("Запрос с несуществующим заказом возвращает ошибку 404")
    public void getOrderWithNonExistentNumber() {
        given()
                .queryParam("t", "000000")
                .get("api/v1/orders/track")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }
}

