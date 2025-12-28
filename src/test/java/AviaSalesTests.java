import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Objects;

import org.openqa.selenium.chrome.ChromeOptions;
import pages.SearchPage;

public class AviaSalesTests {

    @BeforeAll
    static void beforeAll() {
        // Allure listener
        SelenideLogger.addListener("allure", new AllureSelenide());

        // Настройки для CI
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;

        // Устанавливаем remote только если он указан и не пустой
        String remoteUrl = System.getProperty("selenide.remote");
        if (remoteUrl != null && !remoteUrl.trim().isEmpty()) {
            Configuration.remote = remoteUrl;
        }

        // Для CI устанавливаем headless режим
        boolean isCi = System.getProperty("selenide.headless", "false").equals("true");
        Configuration.headless = isCi;

        // Дополнительные настройки для стабильности в CI
        Configuration.browserCapabilities.setCapability("acceptInsecureCerts", true);
        Configuration.pageLoadStrategy = "eager";

        // Уникальный user data directory для избежания конфликтов
        if (isCi) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "--user-data-dir=/tmp/chrome-user-data-" + System.currentTimeMillis(),
                    "--no-sandbox",
                    "--disable-dev-shm-usage"
            );
            Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }
    }


    //@Disabled
    @Test
    void test01ChangeCityFromDirtyFix() {
        Configuration.browser = "firefox";
        //Configuration.pageLoadStrategy = "eager"; // Без полной загрузки не работает
        Configuration.pageLoadTimeout = 180_000; // Увеличиваем время для полной загрузки до 3 минут.
        // Страница нестабильная: иногда быстро загружается, иногда требуется больше минуты
        open("https://aviasales.ru/");
        SelenideElement se = $("#avia_form_origin-input");
        se.shouldBe(Condition.interactable, Duration.ofSeconds(30)); // Проверяем, что элемент interactable,
        // т.е. с ним можно взаимодействовать - добавил, т.к. возникали ошибки, что элемент не interactable
        se.shouldNotBe(Condition.readonly, Duration.ofSeconds(30)); // Проверяем, что элемент доступен для записи,
        // т.е. с ним можно взаимодействовать - добавил, т.к. возникали ошибки, что элемент readonly
        System.out.println(se.getValue());
        String cityTo = "Новосибирск";
        se.setValue(cityTo); // Иногда срабатывает прямо так
        System.out.println(se.getValue());
        // В цикле мы тупо присваиваем значение, пока оно не присвоится.
        // Конечно, надо добавить ограничение на число попыток, чтобы не было бесконечного цикла
        // Например, цикл for от 1 до 100, а потом, если значение не присвоено, то выбрасывать ошибку
        while (cityTo.equals(se.getValue())) {
            sleep(1_000);
            se.click(); // Если кликнуть по этому полю, а потом перед тем, как присваивать значение очистить его,
            // то через какое-то время поле приходит в состояние, когда ему можно присвоить значение, которое требуется
            se.setValue("").setValue(cityTo);
            System.out.println(se.getValue());
        }
    }
    //@Disabled
    @Test
    void test02ChangeCityFromSolution() {
        //Проверим, а если просто подождать, то не будет ли всё хорошо работать
        Configuration.browser = "firefox";
        Configuration.pageLoadTimeout = 180_000;
        for (int i = 0; i < 30; i++) {
            open("https://aviasales.ru/");
            SelenideElement se = $("#avia_form_origin-input");
            se.shouldBe(Condition.interactable, Duration.ofSeconds(30));
            se.shouldNotBe(Condition.readonly, Duration.ofSeconds(30));
            System.out.println("До    : " + se.getValue());
            String cityTo = "Новосибирск";
            se.click();
            System.out.println("Click : " + se.getValue());
            se.sendKeys(Keys.DELETE);
            System.out.println("Delete: " + se.getValue());
            se.sendKeys(cityTo);
            System.out.println("После : " + se.getValue() + "\n");
        }
    }

    @Test
    void test03SimpleSearch() {

        SearchPage sp = new SearchPage();
        sp.setCityFrom("Новосибирс");
        sp.setCityTo("Москв");
        sp.setDates("2026-01-05");
        sp.findTickets();

        sleep(5_000);
    }

    @Test
    void test04() {
        Configuration.browser = "firefox";
        open("https://aviasales.ru");
        sleep(3000);
        if ($("body").text().contains("Тут покупают дешёвые авиабилеты")) {
            System.out.println("ok");
            $("#credential_picker_container").shouldBe(Condition.visible, Duration.ofSeconds(30));
            //switchTo().frame(0);
            switchTo().frame($x("//div[@id='credential_picker_container']//iframe"));
            System.out.println("iframe: " + $("body").text());
            //sleep(300_000);
            $x("//div[@id='close']").click();
        } else {
            switchTo().window(1);
        }

    }

    @Test
    void test05SimpleFail() {
        Assertions.fail("Просто прервали тест с ошибкой");
    }
}