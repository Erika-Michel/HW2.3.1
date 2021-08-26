package ru.netology;

import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import javax.swing.text.NumberFormatter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {
    private Faker faker;

    @BeforeEach
    void setUpAll() {
        faker = new Faker(new Locale("ru-RU"));
    }

    private String deliveryDate(int daysToAdd) {
        LocalDate date = LocalDate.now();
        LocalDate deliveryDate = date.plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String deliveryDateText = deliveryDate.format(formatter);
        return deliveryDateText;
    }

    @Test
    void shouldSubmitRequestWithFullDataAndThanResubmit() {

        open("http://localhost:9999");
        String city = faker.address().city();
        $$("[type='text']").first().setValue(city);
        $("[data-test-id='date'] [pattern]").sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        int firstDate = 3 + (int) (Math.random() * 27);
        $("[data-test-id='date'] [pattern]").setValue(deliveryDate(firstDate));
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        $("[name='name']").setValue(lastName + " " + firstName);
        String phone = faker.phoneNumber().phoneNumber().replaceAll("[()\\s-]+", "");
        $("[name='phone']").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        $("[data-test-id='success-notification']").shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + deliveryDate(firstDate)));

        $$("[type='text']").first().sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $("[name='name']").sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $("[data-test-id='date'] [pattern]").sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $$("[type='text']").first().setValue(city);
        int secondDate = 3 + (int) (Math.random() * 27);
        $("[data-test-id='date'] [pattern]").setValue(deliveryDate(secondDate));
        $("[name='name']").setValue(lastName + " " + firstName);
        $("[name='phone']").setValue(phone);
        $(byText("Запланировать")).click();
        $("[data-test-id= 'replan-notification']")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(byText("Перепланировать")).shouldBe(visible).click();
        $("[data-test-id='success-notification']").shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + deliveryDate(secondDate)));
    }

}
