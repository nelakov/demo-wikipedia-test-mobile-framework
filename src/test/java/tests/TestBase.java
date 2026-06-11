package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import drivers.DriverSettings;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static io.qameta.allure.Allure.step;

public class TestBase {

    @BeforeAll
    public static void setup() {
        Configuration.browser = DriverSettings.resolveDriverClassName(System.getProperty("deviceProvider"));
        Configuration.browserSize = null; // mobile apps have no browser window to size
        addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeEach
    public void startDriver() {
        open();
    }

    @AfterEach
    public void afterEach() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        step("Close driver", Selenide::closeWebDriver);
    }
}
