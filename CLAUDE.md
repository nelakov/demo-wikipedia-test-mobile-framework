# CLAUDE.md

Java + Gradle **Appium + Selenide** UI test framework for the Wikipedia Android app.
Test-only project: all code lives under `src/test/java` (no `src/main`). JUnit 6 + Allure.
Build: **Gradle Kotlin DSL** (`build.gradle.kts`), Java 25 toolchain, Gradle 9.5.1 wrapper.

## Commands

```bash
# Pick ONE device provider via the -DdeviceProvider system property:
gradle clean test -DdeviceProvider=browserstack   # remote BrowserStack farm
gradle clean test -DdeviceProvider=emulator       # local Android emulator
gradle clean test -DdeviceProvider=mobile         # real Samsung device over USB

# Allure report (allure CLI), after a run:
allure serve build/allure-results
```

- Missing/blank/unknown `-DdeviceProvider` → `IllegalArgumentException` with a clear message
  (`drivers/DriverSettings.resolveDriverClassName`).
- `./gradlew` (Gradle 9.5.1 wrapper) or system `gradle` both work.
- `emulator` and `mobile` runs require a local **Appium server on port 4723**
  (`serverUrl=http://localhost:4723/wd/hub`).

## Architecture

Flow: `TestBase.setup()` → `DriverSettings.resolveDriverClassName(deviceProvider)` returns a
`WebDriverProvider` **fully-qualified class name** → assigned to Selenide's
`Configuration.browser`. Selenide then instantiates that provider to build the Appium driver.

| Package | Role |
|---|---|
| `tests/` | `TestBase` (Selenide config, Allure listener, per-test `open()`/`closeWebDriver`), `WikipediaAppiumTests` (4 `@Test`s) |
| `tests/steps/` | `WikiSteps` — `@Step`-annotated page actions (Appium locators: id / accessibilityId / xpath) |
| `drivers/` | `DriverSettings` selector + `BrowserstackMobileDriver` (RemoteWebDriver) + `LocalAndroidDriver` abstract base (UiAutomator2 build logic) with `EmulatorMobileDriver`/`DeviceMobileDriver` subclasses that only supply the config (template method) |
| `config/` | Owner-library config interfaces + `Credentials` factory holding the 3 config singletons |
| `helpers/` | `Attach` (Allure screenshot + page-source attachments) |

`deviceProvider` → driver map (`DriverSettings`): `browserstack`→`BrowserstackMobileDriver`,
`emulator`→`EmulatorMobileDriver`, `mobile`→`DeviceMobileDriver` (Samsung).

## Dependencies (build.gradle.kts)

selenide `7.16.2`, appium-java-client `10.1.1`, junit-jupiter `6.1.0`, owner `1.0.12`,
allure `2.35.2` (+ allure-selenide, aspectjWeaver on), commons-io `2.22.0`,
slf4j-simple `2.0.18`. Allure Gradle plugin `4.1.0`.
Java 25 toolchain pinned (`java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }`).

## Configuration (Owner library)

Configs are loaded from the classpath by `org.aeonbits.owner`. `Credentials.java` creates them;
`configEmulator`/`configSamsung` are built with `System.getProperties()` (so any key is
`-D`-overridable), but `configBrowserstack` is **file-only** (no `-D` override).

| Config interface | Classpath source | Keys |
|---|---|---|
| `BrowserStackConfig` | `config/browserstack.properties` | user, key, app, device, os_version, project, build, name, url |
| `EmulatorConfig` | `emulator.properties` (resources **ROOT**) | platformName, deviceName, platformVersion, locale, language, appPackage, appActivity, appUrl, appPath, serverUrl |
| `SamsungMobileConfig` | `config/samsung.properties` | (same 10 keys as Emulator) |

`EmulatorConfig` and `SamsungMobileConfig` are empty interfaces that only bind `@Config.Sources`;
both `extend LocalAndroidConfig`, where the 10 keys are declared once.

## Allure

`TestBase` registers `AllureSelenide`; `@Step` methods need aspectjWeaver (enabled in
`build.gradle.kts`). `helpers/Attach` attaches the last screenshot + page source after each
test (guarded — skipped when no driver started).

## Gotchas

- **All `*.properties` are gitignored** (`.gitignore`): config files are local-only and must
  be created by hand before the first run. See README for example contents.
- **Config paths: README and code now agree** (verified). Source of truth is the
  `@Config.Sources` annotations in `config/*.java`. Note the non-obvious split: emulator config
  is at the resources **root** (`emulator.properties`), while browserstack and samsung live
  under `config/`.
- **APK auto-downloads** if absent: the local Android drivers fetch from `appUrl` to `appPath`
  when the file is missing (`drivers/LocalAndroidDriver.downloadApkIfMissing`). Committed copy:
  `src/test/resources/apk/app-alpha-universal-release.apk`.
- Locators are tied to the Wikipedia **alpha** package (`org.wikipedia.alpha:id/...`); an app
  update can break `WikiSteps`.
- **JUnit 6 + Allure**: the `AllureJunitPlatform` listener logs a `WARNING` on
  `reportingEntryPublished` under JUnit Platform 6 (Allure ecosystem lag). Non-fatal — JUnit
  isolates listener errors; tests still run and report. Drop `junitVersion` to latest 5.x if it
  becomes a problem.
