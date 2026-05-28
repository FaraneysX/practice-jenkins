plugins {
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.lombok)
    alias(libs.plugins.assembly)
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "ru.denisov.itcompany.Application"
        )
    }
    archiveBaseName.set("app")
    archiveClassifier.set("")
    archiveVersion.set("1.0.0")
}

tasks.bootJar {
    archiveBaseName.set("app")
    archiveVersion.set("1.0.0")
    archiveClassifier.set("")
}

dependencies {
    implementation(project(":core"))

    implementation(libs.bundles.springBoot)
    implementation(libs.logging)

    runtimeOnly(libs.postgres)
    runtimeOnly(libs.openapi)

    testImplementation(libs.springBoot.test)
}
