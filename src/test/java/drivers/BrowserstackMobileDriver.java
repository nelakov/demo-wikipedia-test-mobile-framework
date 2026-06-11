package drivers;

import com.codeborne.selenide.WebDriverProvider;
import config.Credentials;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class BrowserstackMobileDriver implements WebDriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        MutableCapabilities mutableCapabilities = new MutableCapabilities();
        mutableCapabilities.merge(capabilities);

        mutableCapabilities.setCapability("browserstack.appium_version", "1.22.0");
        mutableCapabilities.setCapability("browserstack.user", Credentials.configBrowserstack.user());
        mutableCapabilities.setCapability("browserstack.key", Credentials.configBrowserstack.key());
        mutableCapabilities.setCapability("app", Credentials.configBrowserstack.app());
        mutableCapabilities.setCapability("device", Credentials.configBrowserstack.device());
        mutableCapabilities.setCapability("os_version", Credentials.configBrowserstack.osVersion());
        mutableCapabilities.setCapability("project", Credentials.configBrowserstack.project());
        mutableCapabilities.setCapability("build", Credentials.configBrowserstack.build());
        mutableCapabilities.setCapability("name", Credentials.configBrowserstack.name());

        return new RemoteWebDriver(browserstackUrl(), mutableCapabilities);
    }

    private static URL browserstackUrl() {
        String url = Credentials.configBrowserstack.url();
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid BrowserStack URL: " + url, e);
        }
    }
}
