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

    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")
}

tasks.test {
    useJUnitPlatform()
}