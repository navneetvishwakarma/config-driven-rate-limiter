plugins {
    java
}

group = "com.vkteenvan"
version = "0.1.0"

java {
    // Gradle toolchains let you fix a Java version without managing local JDKs.
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JSON parsing for config-driven policies.
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}
