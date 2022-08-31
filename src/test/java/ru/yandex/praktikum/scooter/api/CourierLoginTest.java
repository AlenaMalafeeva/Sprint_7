package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.scooter.api.CourierSteps;
import ru.yandex.praktikum.scooter.api.clients.CourierClient;
import ru.yandex.praktikum.scooter.api.model.Courier;
import ru.yandex.praktikum.scooter.api.model.CourierCredentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourierLoginTest {
    private final CourierClient courierClient = new CourierClient();
    private final CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private int courierId;

    @Before
    public void init() {
        courier = Courier.getRandomCourier();
    }

    @After
    public void tearDown() {
        if (courierId != 0)
            courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем")
    @Description("Если авторизоваться под несуществующим пользователем, запрос должен вернуть ошибку")
    public void checkLogInWithNonExistingCourier() {
        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());

        String errorMessage = courierClient.login(courierCredentials)
                .then()
                .statusCode(SC_NOT_FOUND)
                .extract()
                .path("message");

        assertEquals("The error message does not match the documentation.", "Учетная запись не найдена", errorMessage);
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("Система должна вернуть ошибку, если неправильно указан пароль")
    public void checkLogInWithIncorrectPassword() {
        Response responseCreate = courierClient.create(courier);
        assertTrue("Courier isn't created", courierSteps.isCourierCreated(responseCreate));

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = courierClient.login(courierCredentials);

        courierId = courierSteps.getCourierId(responseLogin);

        Assert.assertNotEquals("Courier ID can't be null", 0, courierId);

        //формируем пару "правильный логин - неправильный пароль"
        CourierCredentials courierIncorrectCredentials =
                new CourierCredentials(courier.getLogin(), courier.getPassword() + RandomUtils.nextInt(1, 100));

        //запрос на авторизацию с неправильным паролем
        String errorMessage = courierClient.login(courierIncorrectCredentials)
                .then()
                .statusCode(SC_NOT_FOUND)
                .extract()
                .path("message");

        assertEquals("The error message does not match the documentation.", "Учетная запись не найдена", errorMessage);
    }

    @Test
    @DisplayName("Авторизация с неправильным логином")
    @Description("Система должна вернуть ошибку, если неправильно указан логин")
    public void checkLogInWithIncorrectLogin() {
        Response responseCreate = courierClient.create(courier);
        assertTrue("Courier isn't created", courierSteps.isCourierCreated(responseCreate));

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = courierClient.login(courierCredentials);

        courierId = courierSteps.getCourierId(responseLogin);

        Assert.assertNotEquals("Courier ID can't be null", 0, courierId);

        //формируем пару "неправильный логин - правильный пароль"
        CourierCredentials courierIncorrectCredentials =
                new CourierCredentials(courier.getLogin() + RandomUtils.nextInt(1, 100), courier.getPassword());

        //запрос на авторизацию с неправильным логином
        String errorMessage = courierClient.login(courierIncorrectCredentials)
                .then()
                .statusCode(SC_NOT_FOUND)
                .extract()
                .path("message");

        assertEquals("The error message does not match the documentation.", "Учетная запись не найдена", errorMessage);
    }

    @Test
    @DisplayName("Запрос без пароля")
    @Description("Запрос без пароля должен вернуть ошибку")
    public void checkLogInWithoutPassword() {
        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), "");
        String errorMessage = courierClient.login(courierCredentials)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .path("message");

        assertEquals("The error message does not match the documentation.", "Недостаточно данных для входа", errorMessage);
    }

    @Test
    @DisplayName("Запрос без логина")
    @Description("Запрос без логина должен вернуть ошибку")
    public void checkLogInWithoutLogin() {
        CourierCredentials courierCredentials = new CourierCredentials("", courier.getPassword());
        String errorMessage = courierClient.login(courierCredentials)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .path("message");

        assertEquals("The error message does not match the documentation.", "Недостаточно данных для входа", errorMessage);
    }
}
