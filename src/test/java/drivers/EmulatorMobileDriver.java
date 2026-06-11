package drivers;

import config.Credentials;
import config.LocalAndroidConfig;

public class EmulatorMobileDriver extends LocalAndroidDriver {
    @Override
    protected LocalAndroidConfig config() {
        return Credentials.configEmulator;
    }
}
