package tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.steps.WikiSteps;

import static com.codeborne.selenide.Selenide.*;

public class WikipediaAppiumTests extends TestBase {
    private WikiSteps steps = new WikiSteps();

    @Test
    @DisplayName("Add new languages before onboard")
    @Feature("Wiki App Mobile")
    @Owner("ElakovNick")
    @Severity(SeverityLevel.NORMAL)
    void addNewLanguagesBeforeOnboard() {
        steps.addNewLanguage();
        steps.addNewLanguageInTheLanguageList();
        steps.chooseLanguage();
        steps.checkLanguage();
        steps.tapBackButton();
        steps.checkListSize();
    }

    @Test
    @DisplayName("Check text on the onboard pages")
    @Feature("Wiki App Mobile")
    @Owner("ElakovNick")
    @Severity(SeverityLevel.CRITICAL)
    void aboutWikiPageTest() {
        steps.checkPrimaryText("The Free Encyclopedia …in over 300 languages");
        steps.clickForwardButton();
        steps.checkPrimaryText("New ways to explore");
        steps.clickForwardButton();
        steps.checkPrimaryText("Reading lists with sync");
        steps.clickForwardButton();
        steps.checkPrimaryText("Send anonymous data");
    }

    @Test
    @DisplayName("Search field - input 'Appium'")
    @Feature("Wiki App Mobile")
    @Owner("ElakovNick")
    @Severity(SeverityLevel.CRITICAL)
    void searchTest() {
        back();
        steps.searchWikiClick();
        steps.typeSearchQuery("Appium");
        steps.assertResultsExist();
    }

    @Test
    @DisplayName("Check languages in the app")
    @Feature("Wiki App Mobile")
    @Owner("ElakovNick")
    @Severity(SeverityLevel.NORMAL)
    void checkLanguage() {
        back();
        steps.searchWikiClick();
        steps.typeSearchQuery("Appium");
        steps.assertLanguageIsEN();
    }
}
