package ru.yandex.praktikum.scooter.api.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.scooter.api.model.Orders;

import static io.restassured.RestAssured.given;

public class OrdersClient extends BaseApiClient {

    @Step("Запрос на создание заказа {order}")
    public Response create(Orders order) {
        return given()
                .spec(getReqSpec())
                .body(order)
                .when()
                .post(BASE_URL + "/api/v1/orders");
    }

    @Step("Запрос на получение списка заказов")
    public Response getListOfOrders() {
        return given()
                .spec(getReqSpec())
                .get(BASE_URL + "/api/v1/orders");
    }
}
