package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.scooter.api.clients.OrdersClient;
import ru.yandex.praktikum.scooter.api.model.Orders;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final List<String> colors;
    private final int expectedStatusCode;
    private final OrdersClient ordersClient = new OrdersClient();

    public CreateOrderTest(List<String> colors, int expectedStatusCode) {
        this.colors = colors;
        this.expectedStatusCode = expectedStatusCode;
    }
    @Parameterized.Parameters
    public static Object[][] getSumData() {
        return new Object[][] {
                {List.of("BLACK"), SC_CREATED},
                {List.of("GREY"), SC_CREATED},
                {List.of("BLACK", "GRAY"), SC_CREATED},
                {List.of(), SC_CREATED}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    public void checkOrderCreate() {
        Orders order = Orders.getRandomOrder(colors);

        int track = ordersClient.create(order)
                .then().statusCode(expectedStatusCode)
                .extract()
                .path("track");

        Assert.assertNotEquals("Track can't be null", 0, track);
    }
}