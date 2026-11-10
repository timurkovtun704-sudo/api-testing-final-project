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
import static org.hamcrest.Matchers.notNullValue;

public class СourierLoginTest {
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
                    .delete("/api/v1/courier/{id}");
        }
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("При попытке авторизоваться с существующими и полностью заполненными полями, успешный запрос возвращает код 200 и id курьера")
    public void courierLogin() {
        File json = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier/login");
        courierId = response.then().extract().path("id");
        response.then().assertThat().statusCode(200).body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неправильным логином")
    @Description("При попытке авторизоваться с неправильным логином возвращает ошибку с кодом 404")
    public void courierLoginWithErrorLogin(){
        File json = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        File json2 = new File("src/main/resources/courierlogin/courierLoginWithErrorLogin.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json2)
                .post("api/v1/courier/login");
        courierId = response.then().extract().path("id");
        response.then().assertThat().statusCode(404);
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("При попытке авторизоваться с неправильным паролем возвращает ошибку с кодом 404")
    public void courierLoginWithErrorPassword(){
        File json = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        File json2 = new File("src/main/resources/courierlogin/courierLoginWithErrorPassword.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json2)
                .post("api/v1/courier/login");
        courierId = response.then().extract().path("id");
        response.then().assertThat().statusCode(404);
    }

    @Test
    @DisplayName("Авторизация с пустым паролем")
    @Description("При попытке авторизоваться с пустым паролем возвращает ошибку с кодом 400")
    public void courierLoginWithEmptyPassword(){
        File json = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        File json2 = new File("src/main/resources/courierlogin/courierLoginWithEmptyPassword.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json2)
                .post("api/v1/courier/login");
        courierId = response.then().extract().path("id");
        response.then().assertThat().statusCode(400);
    }

    @Test
    @DisplayName("Авторизация с пустым логином")
    @Description("При попытке авторизоваться с пустым паролем возвращает ошибку с кодом 400")
    public void courierLoginWithEmptylogin(){
        File json = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        File json2 = new File("src/main/resources/courierlogin/courierLoginWithEmptyLogin.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json2)
                .post("api/v1/courier/login");
        courierId = response.then().extract().path("id");
        response.then().assertThat().statusCode(400);
    }

    @Test
    @DisplayName("Авторизация несуществующего профиля курьера")
    @Description("При попытке авторизовать незарегестрированного курьера возвращает ошибку 404")
    public void loginWithUnknownCourier(){
        File json = new File("src/main/resources/courierlogin/courierLogin.json");
        given()
                .header("Content-Type", "application/json")
                .body(json)
                .post("api/v1/courier");
        File json2 = new File("src/main/resources/courierlogin/loginWithNonExistCourier.json");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(json2)
                .post("api/v1/courier/login");
        courierId = response.then().extract().path("id");
        response.then().assertThat().statusCode(404);
    }
}
