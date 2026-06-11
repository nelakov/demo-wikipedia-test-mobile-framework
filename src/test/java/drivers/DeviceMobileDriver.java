package drivers;

import config.Credentials;
import config.LocalAndroidConfig;

public class DeviceMobileDriver extends LocalAndroidDriver {
    @Override
    protected LocalAndroidConfig config() {
        return Credentials.configSamsung;
    }
}
