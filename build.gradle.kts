plugins {
    java
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.dependencyManagement) apply false
    alias(libs.plugins.lombok) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.assembly) apply false
}

allprojects {
    group = "ru.denisov"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    pluginManager.apply("java")
    pluginManager.apply("jacoco")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading")

        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.test)

        reports {
            html.required.set(true)
            csv.required.set(true)
            xml.required.set(true)
        }
    }

    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        violationRules {
            rule {
                limit {
                    minimum =
                        "0.50".toBigDecimal() // Требуем минимум 50% покрытия
                }
            }
        }
    }
}
