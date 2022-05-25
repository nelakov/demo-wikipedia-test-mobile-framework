package drivers;

public class DriverSettings {
    public static String getDeviceProvider(String deviceProvider) {
        if (deviceProvider.equals("mobile")) {
            return DeviceMobileDriver.class.getName();
        }

        if (deviceProvider.equals("browserstack")) {
            return BrowserstackMobileDriver.class.getName();
        }

        if (deviceProvider.equals("emulator")) {
            return EmulatorMobileDriver.class.getName();
        }
        throw new RuntimeException("Didn't select device");
    }
}