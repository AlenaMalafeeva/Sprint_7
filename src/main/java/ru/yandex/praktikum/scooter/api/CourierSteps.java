package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.scooter.api.clients.CourierClient;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierSteps {
    private final CourierClient courierClient = new CourierClient();

    @Step("Проверка авторизации")
    public int getCourierId(Response response) {
        return response.then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

    @Step("Проверка успешного создания курьера")
    public boolean isCourierCreated(Response response) {
        return response.then()
                .statusCode(SC_CREATED)
                .extract()
                .path("ok");
    }
}
