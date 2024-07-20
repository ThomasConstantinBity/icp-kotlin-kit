plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":icp_kotlin_kit:icp_candid"))
    implementation(project(":icp_kotlin_kit:icp_cryptography"))

    implementation(libs.kotlin.reflect)
    implementation(libs.bundles.retrofit)
    implementation(libs.jackson.dataformat.cbor)

    testImplementation(libs.bundles.junit.test)
    testImplementation(libs.bundles.kotlinx.test)

    implementation(libs.logging.interceptor)
}

tasks.test {
    useJUnitPlatform()
}