package drivers;

public final class DriverSettings {
    private DriverSettings() {
    }

    public static String resolveDriverClassName(String deviceProvider) {
        if (deviceProvider == null || deviceProvider.isBlank()) {
            throw new IllegalArgumentException(
                    "System property 'deviceProvider' is required: emulator | browserstack | mobile");
        }
        return switch (deviceProvider) {
            case "emulator" -> EmulatorMobileDriver.class.getName();
            case "browserstack" -> BrowserstackMobileDriver.class.getName();
            case "mobile" -> DeviceMobileDriver.class.getName();
            default -> throw new IllegalArgumentException("Unknown deviceProvider: " + deviceProvider);
        };
    }
}
