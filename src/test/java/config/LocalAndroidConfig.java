package config;

import org.aeonbits.owner.Config;

/**
 * Shared key contract for any locally-driven Android device (emulator or physical phone).
 * Concrete interfaces only bind a {@code @Config.Sources} file; the keys live here once.
 */
public interface LocalAndroidConfig extends Config {
    @Key("platformName")
    String platformName();

    @Key("deviceName")
    String deviceName();

    @Key("platformVersion")
    String platformVersion();

    @Key("locale")
    String locale();

    @Key("language")
    String language();

    @Key("appPackage")
    String appPackage();

    @Key("appActivity")
    String appActivity();

    @Key("appUrl")
    String appUrl();

    @Key("appPath")
    String appPath();

    @Key("serverUrl")
    String serverUrl();
}
