import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(tegralLibs.niwen.lexer)
    implementation(tegralLibs.niwen.parser)
    implementation(libs.bouncycastle)
    implementation(libs.commons.codec)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.retrofit)
    implementation(libs.jackson.dataformat.cbor)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.logging.interceptor)

    testImplementation(libs.bundles.junit.test)
    testImplementation(libs.bundles.kotlinx.test)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.bity"
            artifactId = "icp_kotlin_kit"
            version = "1.0.1"
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}