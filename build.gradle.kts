plugins {
    java
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.lombok)
    alias(libs.plugins.versions)
}

group = "ru.denisov"
version = "1.0.0"
description = "it-company"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.springBoot)
    implementation(libs.openapi)

    runtimeOnly(libs.postgres)

    testImplementation(libs.springBoot.test)
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}
