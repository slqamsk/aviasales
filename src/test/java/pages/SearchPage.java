package pages;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
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
        open(url);
        getWebDriver().manage().window().maximize();
        this.cookieButton.shouldBe(Condition.interactable, Duration.ofSeconds(30));
        this.cookieButton.click();
    }

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

    public void setDates(String startDate) {
        this.startDateField.click();

        this.startDateTR = $x("//td[@data-day='" + startDate + "']//button");
        this.startDateTR.shouldBe(Condition.exist, Duration.ofSeconds(30));
        this.startDateTR.shouldBe(Condition.interactable, Duration.ofSeconds(30));

        this.startDateTR.click();

        this.oneWayButton.shouldBe(Condition.exist, Duration.ofSeconds(30));
        this.oneWayButton.shouldBe(Condition.interactable, Duration.ofSeconds(30));
        this.oneWayButton.click();
    }

    public void findTickets() {
        this.ostrovokLable.click();
        this.findTicketsButton.click();

        this.recomendedPrice.shouldBe(Condition.visible, Duration.ofSeconds(30));
        System.out.println("Стоимость рекомендованного: " + this.recomendedPrice.text());
    }
}
