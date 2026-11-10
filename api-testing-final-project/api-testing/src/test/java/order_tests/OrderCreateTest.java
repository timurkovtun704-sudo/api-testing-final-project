package order_tests;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private final List<String> scooterColor;
    public OrderCreateTest(List<String> scooterColor) {
        this.scooterColor = scooterColor;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Создание заказа с различными вариациями цветов")
    @Description("Проверка, что заказ создается с одним цветом, обоими или без цветов, возвращая track-номер")
    public void orderCreationWithDifferentColors() {
        Map<String, Object> orderBody = new HashMap<>();
        orderBody.put("firstName", "Timur");
        orderBody.put("lastName", "Kovtun");
        orderBody.put("address", "Central str. 10");
        orderBody.put("metroStation", 4);
        orderBody.put("phone", "+7 999 999 99 99");
        orderBody.put("rentTime", 5);
        orderBody.put("deliveryDate", "2026-11-20");
        orderBody.put("comment", "Please call before arrival");
        orderBody.put("color", scooterColor);

        given()
                .header("Content-Type", "application/json")
                .body(orderBody)
                .post("api/v1/orders")
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }
}

