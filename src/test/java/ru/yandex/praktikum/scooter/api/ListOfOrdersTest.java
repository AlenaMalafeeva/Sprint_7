package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.scooter.api.clients.OrdersClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasKey;

public class ListOfOrdersTest {
    private final OrdersClient ordersClient = new OrdersClient();

    @Test
    @DisplayName("В тело ответа возвращается список заказов")
    public void checkResponseBodyListOfOrders() {

        ordersClient.getListOfOrders()
                .then().statusCode(SC_OK)
                .assertThat().body("$", hasKey("orders"));
    }
}
