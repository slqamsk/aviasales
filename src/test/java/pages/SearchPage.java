package pages;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Objects;

public class SearchPage {
    String url;
    int attempts;
    SelenideElement cookieButton, cityFrom, cityTo,
            startDateField, startDateTR,
            oneWayButton, ostrovokLable, findTicketsButton,
            recomendedPrice;


    public SearchPage() {
        //Определяем поля
        this.attempts = 20; //Число попыток ввести значение в поле
        this.url = "https://aviasales.ru";
        this.cookieButton = $x("//div[.='Да без проблем']/..");
        this.cityFrom = $("#avia_form_origin-input");
        this.cityTo = $("#avia_form_destination-input");
        this.startDateField = $x("//button[@data-test-id='start-date-field']");
        this.oneWayButton = $x("//span[.='Выбрать в одну сторону']/../..");
        this.ostrovokLable = $x("//span[contains(.,'Открыть Островок!')]");
        this.findTicketsButton = $x("//button[@data-test-id='form-submit']");
        this.recomendedPrice = $x("//span[.='Рекомендуемый']/ancestor::div[@data-test-id='ticket-preview']//div[@data-test-id='price']");

        //Открываем страницу
        Configuration.pageLoadStrategy = "eager";
        Configuration.browser = "firefox";
        open(url);

        sleep(5_000);
        if ($("body").text().contains("Тут покупают дешёвые авиабилеты")) {
            System.out.println("ok");
            $("#credential_picker_container").shouldBe(Condition.visible, Duration.ofSeconds(30));
            //switchTo().frame(0);
            switchTo().frame($x("//div[@id='credential_picker_container']//iframe"));
            System.out.println("iframe: " + $("body").text());
            //sleep(300_000);
            $x("//div[@id='close']").click();
            switchTo().defaultContent();
        } else {
            switchTo().window(1);
        }
        getWebDriver().manage().window().maximize();
        //sleep(600_000);
        this.cookieButton.shouldBe(Condition.exist, Duration.ofSeconds(30));
        this.cookieButton.shouldBe(Condition.interactable, Duration.ofSeconds(30));
        this.cookieButton.click();
    }

    @Step("Установить город ОТКУДА")
    public void setCityFrom(String cityFrom) {
        for (int i = 0; i < this.attempts; i++) {
            this.cityFrom.shouldBe(Condition.interactable, Duration.ofSeconds(30));
            this.cityFrom.shouldNotBe(Condition.readonly, Duration.ofSeconds(30));
            this.cityFrom.click();
            this.cityFrom.sendKeys(Keys.DELETE);
            this.cityFrom.sendKeys(cityFrom);

            sleep(1_000);
            if (cityFrom.equals(this.cityFrom.getValue())) {
                sleep(5_000);
                this.cityFrom.sendKeys(Keys.ARROW_DOWN);
                sleep(1_000);
                this.cityFrom.sendKeys(Keys.ENTER);
                sleep(1_000);
                break;
            }
        }
    }

    @Step("Установить город КУДА")
    public void setCityTo(String cityTo) {
        this.cityTo.shouldBe(Condition.interactable, Duration.ofSeconds(30));
        this.cityTo.shouldNotBe(Condition.readonly, Duration.ofSeconds(30));
        this.cityTo.click();
        this.cityTo.sendKeys(Keys.DELETE);
        this.cityTo.sendKeys(cityTo);
        sleep(5_000);
        this.cityTo.sendKeys(Keys.ARROW_DOWN);
        sleep(1_000);
        this.cityTo.sendKeys(Keys.ENTER);
        sleep(1_000);
    }

    @Step("Установить дату")
    public void setDates(String startDate) {
        this.startDateTR = $x("//td[@data-day='" + startDate + "']//button");

        for (int i = 0; i < this.attempts; i++) {
            sleep(3_000);
            this.startDateField.click();
            System.out.println("Попытка: " + i);
            if (this.startDateTR.exists()) {
                break;
            }
        }

        this.startDateTR.shouldBe(Condition.exist, Duration.ofSeconds(10));
        this.startDateTR.shouldBe(Condition.interactable, Duration.ofSeconds(10));

        this.startDateTR.click();

        this.oneWayButton.shouldBe(Condition.exist, Duration.ofSeconds(10));
        this.oneWayButton.shouldBe(Condition.interactable, Duration.ofSeconds(10));
        this.oneWayButton.click();
    }

    @Step("Найти билеты")
    public void findTickets() {
        this.ostrovokLable.click();
        this.findTicketsButton.click();

        this.recomendedPrice.shouldBe(Condition.visible, Duration.ofSeconds(30));
        System.out.println("Стоимость рекомендованного: " + this.recomendedPrice.text());
    }
}
