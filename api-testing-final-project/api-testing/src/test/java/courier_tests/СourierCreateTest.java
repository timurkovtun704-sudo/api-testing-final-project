package courier_tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class СourierCreateTest {
    private Integer courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            given()
                    .pathParam("id", courierId)
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Успешное создание курьера со всеми полями")
    @Description("Проверка, что курьера можно создать, успешный запрос возвращает ok: true и правильный код ответа 201")
    public void canCourierCreate() {
        File json = new File("src/main/resources/couriercreate/createCourier.json");
        Response response = given()
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

        response.then().assertThat().body("ok", equalTo(true)).and().statusCode(201);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void cantSimillarCourierCreate() {
        File json = new File("src/main/resources/couriercreate/createCourier.json");
        Response response = given()
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

        Response response2 = given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");

        response.then().assertThat().body("ok", equalTo(true)).and().statusCode(201);
        response2.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой.")).and().statusCode(409);
    }

    @Test
    @DisplayName("Нельзя создать профиль курьера без пароля")
    @Description("Проверка, что если попытаться создать курьера без пароля, возвращается ошибка")
    public void cantCreateCourierWithoutPasssword() {
        File json = new File("src/main/resources/couriercreate/createCourierWithoutPassword.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи")).and().statusCode(400);
    }

    @Test
    @DisplayName("Нельзя создать профиль курьера без логина")
    @Description("Проверка, что если попытаться создать курьера без логина, возвращается ошибка")
    public void cantCreateCourierWithoutLogin() {
        File json = new File("src/main/resources/couriercreate/createCourierWithoutLogin.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи")).and().statusCode(400);
    }

    @Test
    @DisplayName("Нельзя создать профиль курьера с существующим логином")
    @Description("Проверка, что если попытаться создать курьера с существующим лоигном, возвращается ошибка")
    public void createCourierWithExistLogin() {
        File json = new File("src/main/resources/couriercreate/createCourier.json");
        Response response = given()
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
        response.then().assertThat().body("ok", equalTo(true)).and().statusCode(201);
        File file = new File("src/main/resources/couriercreate/createCourierWithExistLogin.json");
        Response response2 = given()
                .header("Content-Type", "application/json")
                .body(file)
                .post("api/v1/courier");
        response2.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой.")).and().statusCode(409);
    }
}

