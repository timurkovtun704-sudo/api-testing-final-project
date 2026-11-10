package courier_tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
public class СourierDeleteTest {
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        File json = new File("src/main/resources/courierdelete/beforeCourierCreate.json");
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
            given().pathParam("id", courierId).delete("/api/v1/courier/{id}");
        }
    }

    @Test
    @DisplayName("Удаление существующего пользователя")
    @Description("Проверка, что профиль курьера можно удалить, успешный запрос возвращает ok: true и правильный код ответа 200")
    public void courierDelete() {
        given()
                .pathParam("id", courierId)
                .delete("/api/v1/courier/{id}")
                .then()
                .assertThat()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Удаление курьера с пустым id")
    @Description("Проверка, что при попытке удаления профиля курьера с пустым id возвращается ошибка 404")
    public void courierDeleteWithEmptyId() {
        given()
                .pathParam("id", "")
                .delete("/api/v1/courier/{id}")
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    @DisplayName("Удаление несуществующего профиля курьера")
    @Description("Проверка, что при попытке удаления профиля курьера с несуществующим id возвращается ошибка 404")
    public void courierDeleteWithNonExistentId() {
        given()
                .pathParam("id", "000000")
                .delete("/api/v1/courier/{id}")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет."));
    }
}

