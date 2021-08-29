package ru.netology.delivery.test;

import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $$("[type='text']").first().setValue(validUser.getCity());
        $("[data-test-id='date'] [pattern]").sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $("[data-test-id='date'] [pattern]").setValue(firstMeetingDate);
        $("[name='name']").setValue(validUser.getName());
        $("[name='phone']").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        $("[data-test-id='success-notification']").shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));

        $$("[type='text']").first().sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $("[name='name']").sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $("[data-test-id='date'] [pattern]").sendKeys(Keys.CONTROL + "A", Keys.DELETE);
        $$("[type='text']").first().setValue(validUser.getCity());
        $("[data-test-id='date'] [pattern]").setValue(secondMeetingDate);
        $("[name='name']").setValue(validUser.getName());
        $("[name='phone']").setValue(validUser.getPhone());
        $(byText("Запланировать")).click();
        $("[data-test-id= 'replan-notification']")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(byText("Перепланировать")).shouldBe(visible).click();
        $("[data-test-id='success-notification']").shouldBe(visible)
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
