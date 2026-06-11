package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config/samsung.properties"})
public interface SamsungMobileConfig extends LocalAndroidConfig {
}
