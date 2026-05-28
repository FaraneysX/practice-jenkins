plugins {
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.lombok)
}

dependencies {
    implementation(platform(libs.springBoot.bom))
    implementation(libs.springBoot.dataJpa)
}
