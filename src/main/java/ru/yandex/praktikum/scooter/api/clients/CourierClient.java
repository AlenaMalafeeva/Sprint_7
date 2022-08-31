package ru.yandex.praktikum.scooter.api.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.scooter.api.model.Courier;
import ru.yandex.praktikum.scooter.api.model.CourierCredentials;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierClient extends BaseApiClient {

    @Step("Запрос за создание курьера {courier}")
    public Response create(Courier courier) {
        return given()
                .spec(getReqSpec())
                .body(courier)
                .when()
                .post(BASE_URL + "/api/v1/courier");
    }

    @Step("Запрос на авторизацию в системе {courierCredentials}")
    public Response login(CourierCredentials courierCredentials) {
        return given()
                .spec(getReqSpec())
                .body(courierCredentials)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");
    }

    @Step("Запрос на удаление курьера с id - {courierId}")
    public boolean delete(int courierId) {
        return given()
                .spec(getReqSpec())
                .when()
                .delete(BASE_URL + "/api/v1/courier/" + courierId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("ok");
    }
}
