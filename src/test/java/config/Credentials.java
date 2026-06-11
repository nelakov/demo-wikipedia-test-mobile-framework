package config;

import org.aeonbits.owner.ConfigFactory;

public final class Credentials {
    private Credentials() {
    }

    public static final BrowserStackConfig configBrowserstack =
            ConfigFactory.create(BrowserStackConfig.class);
    public static final EmulatorConfig configEmulator =
            ConfigFactory.create(EmulatorConfig.class, System.getProperties());
    public static final SamsungMobileConfig configSamsung =
            ConfigFactory.create(SamsungMobileConfig.class, System.getProperties());
}
