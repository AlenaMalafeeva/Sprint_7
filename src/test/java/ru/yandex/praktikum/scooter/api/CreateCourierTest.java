package ru.yandex.praktikum.scooter.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.scooter.api.clients.CourierClient;
import ru.yandex.praktikum.scooter.api.model.Courier;
import ru.yandex.praktikum.scooter.api.model.CourierCredentials;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.praktikum.scooter.api.model.Courier.getRandomCourier;

public class CreateCourierTest {
    private final CourierClient courierClient = new CourierClient();
    private final CourierSteps courierSteps = new CourierSteps();
    private Courier courier;
    private int courierId;

    @Before
    public void init() {
        courier = getRandomCourier();
    }

    @After
    public void tearDown() {
        if (courierId != 0)
            courierClient.delete(courierId);
    }

    //Проверяем возможность создать курьера
    @Test
    @DisplayName("Создание курьера")
    @Description("Проверяем возможность создать курьера")
    public void checkCourierCreateWithValidData() {

        Response responseCreate = courierClient.create(courier);
        assertTrue("Courier isn't created", courierSteps.isCourierCreated(responseCreate));

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = courierClient.login(courierCredentials);

        courierId = courierSteps.getCourierId(responseLogin);

        Assert.assertNotEquals("Courier ID can't be null", 0, courierId);
    }

    @Test
    @DisplayName("Создание с повторяющимися данными")
    @Description("Проверяем, что нельзя создать двух одинаковых курьеров")
    public void checkCreateDuplicateCourier() {

        Response responseCreate = courierClient.create(courier);
        assertTrue("Courier isn't created", courierSteps.isCourierCreated(responseCreate));

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = courierClient.login(courierCredentials);

        courierId = courierSteps.getCourierId(responseLogin);

        Assert.assertNotEquals("Courier ID can't be null", 0, courierId);

        int responseStatusCodeDuplicateCourier = courierClient.create(courier).statusCode();
        assertEquals(SC_CONFLICT, responseStatusCodeDuplicateCourier);
    }

    //
    @Test
    @DisplayName("Создание курьера с повторяющимся логином")
    @Description("Если создать пользователя с логином, который уже есть, должна возвращаться ошибка")
    public void checkCourierCreateWithDuplicateLogin() {

        Response responseCreate = courierClient.create(courier);
        assertTrue("Courier isn't created", courierSteps.isCourierCreated(responseCreate));

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = courierClient.login(courierCredentials);

        courierId = courierSteps.getCourierId(responseLogin);

        Assert.assertNotEquals("Courier ID can't be null", 0, courierId);

        Courier courierWithDuplicateLogin = getRandomCourier();
        courierWithDuplicateLogin.setLogin(courier.getLogin());

        String errorMessage = courierClient.create(courierWithDuplicateLogin)
                .then()
                .statusCode(SC_CONFLICT)
                .extract()
                .path("message");

        assertEquals("The error message does not match the documentation.", "Этот логин уже используется", errorMessage);
    }

    @Test
    @DisplayName("Запрос без имени курьера")
    @Description("Если одного из полей нет, запрос должен возвращать ошибку")
    public void checkCourierCreateWithoutFirstName() {
        courier.setFirstName("");
        String errorMessage = courierClient.create(courier)
                .then().statusCode(SC_BAD_REQUEST)
                .extract()
                .path("message");
        assertEquals("The error message does not match the documentation.", "Недостаточно данных для создания учетной записи", errorMessage);
    }

    @Test
    @DisplayName("Запрос без логина")
    @Description("Если одного из полей нет, запрос должен возвращать ошибку")
    public void checkCourierCreateWithoutLogin() {
        courier.setLogin("");
        String errorMessage = courierClient.create(courier)
                .then().statusCode(SC_BAD_REQUEST)
                .extract()
                .path("message");
        assertEquals("The error message does not match the documentation.", "Недостаточно данных для создания учетной записи", errorMessage);
    }

    //если нет пароля
    @Test
    @DisplayName("Запрос без пароля")
    @Description("Если одного из полей нет, запрос должен возвращать ошибку")
    public void checkCourierCreateWithoutPassword() {
        courier.setPassword("");
        String errorMessage = courierClient.create(courier)
                .then().statusCode(SC_BAD_REQUEST)
                .extract()
                .path("message");
        assertEquals("The error message does not match the documentation.", "Недостаточно данных для создания учетной записи", errorMessage);
    }
}