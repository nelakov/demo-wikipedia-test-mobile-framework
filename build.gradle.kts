import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    id("io.qameta.allure") version "4.1.0"
}

repositories {
    mavenCentral()
}

val allureVersion = "2.29.1"
val selenideVersion = "7.9.3"
val appiumVersion = "9.4.0"
val junitVersion = "6.1.0"
val slf4jVersion = "2.0.18"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

allure {
    adapter {
        aspectjWeaver = true
        allureJavaVersion = allureVersion
        frameworks {
            junit5 {
                adapterVersion = allureVersion
            }
        }
    }
}

dependencies {
    testImplementation("org.aeonbits.owner:owner:1.0.12")
    testImplementation("com.codeborne:selenide:$selenideVersion")
    testImplementation("io.qameta.allure:allure-selenide:$allureVersion")
    testImplementation("io.appium:java-client:$appiumVersion")
    testImplementation("commons-io:commons-io:2.22.0")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperties(System.getProperties().mapKeys { it.key.toString() })
    useJUnitPlatform()

    testLogging {
        events(
            TestLogEvent.STARTED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.SHORT
    }
}
