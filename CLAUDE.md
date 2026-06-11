# CLAUDE.md

Java + Gradle **Appium + Selenide** UI test framework for the Wikipedia Android app.
Test-only project: all code lives under `src/test/java` (no `src/main`). JUnit 5 + Allure.

## Commands

```bash
# Pick ONE device provider via the -DdeviceProvider system property:
gradle clean test -DdeviceProvider=browserstack   # remote BrowserStack farm
gradle clean test -DdeviceProvider=emulator       # local Android emulator
gradle clean test -DdeviceProvider=mobile         # real Samsung device over USB

# Allure report (allure CLI), after a run:
allure serve build/allure-results
```

- Missing/unknown `-DdeviceProvider` → `RuntimeException("Didn't select device")`
  (`drivers/DriverSettings.java`).
- README uses system `gradle`; `./gradlew` also works but is gitignored.
- `emulator` and `mobile` runs require a local **Appium server on port 4723**
  (`serverUrl=http://localhost:4723/wd/hub`).

## Architecture

Flow: `TestBase.setup()` → `DriverSettings.getDeviceProvider(deviceProvider)` returns a
`WebDriverProvider` **fully-qualified class name** → assigned to Selenide's
`Configuration.browser`. Selenide then instantiates that provider to build the Appium driver.

| Package | Role |
|---|---|
| `tests/` | `TestBase` (Selenide config, Allure listener, per-test `open()`/`closeWebDriver`), `WikipediaAppiumTests` (4 `@Test`s) |
| `tests/steps/` | `WikiSteps` — `@Step`-annotated page actions (Appium locators: id / accessibilityId / xpath) |
| `drivers/` | `DriverSettings` selector + 3 `WebDriverProvider`s: `BrowserstackMobileDriver` (RemoteWebDriver), `EmulatorMobileDriver` + `DeviceMobileDriver` (local `AndroidDriver`, UiAutomator2) |
| `config/` | Owner-library config interfaces + `Credentials` factory holding the 3 config singletons |
| `helpers/` | `Attach` (Allure screenshot/pageSource/video) + `Browserstack` (session video URL via REST) |

`deviceProvider` → driver map (`DriverSettings`): `browserstack`→`BrowserstackMobileDriver`,
`emulator`→`EmulatorMobileDriver`, `mobile`→`DeviceMobileDriver` (Samsung).

## Dependencies (build.gradle)

selenide `6.4.0`, appium-java-client `8.0.0`, junit-jupiter `5.8.2`, owner `1.0.12`,
allure `2.17.3` (+ allure-selenide, aspectjWeaver on), rest-assured `4.5.1`,
commons-io `2.11.0`, slf4j-simple `1.7.32`. Allure Gradle plugin `2.9.6`.
No Java toolchain is pinned in build.gradle — uses the JDK on PATH.

## Configuration (Owner library)

Configs are loaded from the classpath by `org.aeonbits.owner`. `Credentials.java` creates them;
`configEmulator`/`configSamsung` are built with `System.getProperties()` (so any key is
`-D`-overridable), but `configBrowserstack` is **file-only** (no `-D` override).

| Config interface | Classpath source | Keys |
|---|---|---|
| `BrowserStackConfig` | `config/browserstack.properties` | user, key, app, device, os_version, project, build, name, url |
| `EmulatorConfig` | `emulator.properties` (resources **ROOT**) | platformName, deviceName, platformVersion, locale, language, appPackage, appActivity, appUrl, appPath, serverUrl |
| `SamsungMobileConfig` | `config/samsung.properties` | (same 10 keys as Emulator) |

## Allure + BrowserStack video

`TestBase` registers `AllureSelenide`; `@Step` methods need aspectjWeaver (enabled in
build.gradle). `helpers/Attach` attaches last screenshot + page source after each test.
`Attach.video()` embeds the BrowserStack recording, fetched by `Browserstack.videoUrl()`
via REST (`api-cloud.browserstack.com/app-automate/sessions/{id}.json`, basic auth).

## Gotchas

- **All `*.properties` are gitignored** (`.gitignore`): config files are local-only and must
  be created by hand before the first run. See README for example contents.
- **Config paths: README and code now agree** (verified). Source of truth is the
  `@Config.Sources` annotations in `config/*.java`. Note the non-obvious split: emulator config
  is at the resources **root** (`emulator.properties`), while browserstack and samsung live
  under `config/`.
- **APK auto-downloads** if absent: `EmulatorMobileDriver`/`DeviceMobileDriver` fetch from
  `appUrl` to `appPath` when the file is missing. Committed copy:
  `src/test/resources/apk/app-alpha-universal-release.apk`.
- `tests/samples/*.java` are **fully commented-out** dead reference (contain old hardcoded
  BrowserStack creds in comments) — not run, not maintained.
- Locators are tied to the Wikipedia **alpha** package (`org.wikipedia.alpha:id/...`); an app
  update can break `WikiSteps`.
