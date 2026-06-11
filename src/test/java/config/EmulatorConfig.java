package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:emulator.properties"})
public interface EmulatorConfig extends LocalAndroidConfig {
}
