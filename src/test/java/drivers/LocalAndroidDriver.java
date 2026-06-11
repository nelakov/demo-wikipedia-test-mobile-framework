package drivers;

import com.codeborne.selenide.WebDriverProvider;
import config.LocalAndroidConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

/**
 * Builds a local Appium {@link AndroidDriver} (emulator or physical device).
 * Subclasses supply only the {@link LocalAndroidConfig} source; the build logic lives here once.
 */
abstract class LocalAndroidDriver implements WebDriverProvider {

    protected abstract LocalAndroidConfig config();

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        LocalAndroidConfig config = config();

        UiAutomator2Options options = new UiAutomator2Options();
        options.merge(capabilities);
        options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
        options.setPlatformName(config.platformName());
        options.setDeviceName(config.deviceName());
        options.setPlatformVersion(config.platformVersion());
        options.setApp(downloadApkIfMissing().getAbsolutePath());
        options.setLocale(config.locale());
        options.setLanguage(config.language());
        options.setAppPackage(config.appPackage());
        options.setAppActivity(config.appActivity());

        return new AndroidDriver(appiumServerUrl(), options);
    }

    private URL appiumServerUrl() {
        String serverUrl = config().serverUrl();
        try {
            return URI.create(serverUrl).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Appium server URL: " + serverUrl, e);
        }
    }

    private File downloadApkIfMissing() {
        File apk = new File(config().appPath());
        if (apk.exists()) {
            return apk;
        }
        String appUrl = config().appUrl();
        try (InputStream in = URI.create(appUrl).toURL().openStream()) {
            copyInputStreamToFile(in, apk);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to download APK from " + appUrl, e);
        }
        return apk;
    }
}
