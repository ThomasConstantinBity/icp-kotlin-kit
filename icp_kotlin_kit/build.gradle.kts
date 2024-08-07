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
    implementation(libs.bouncycastle)
    implementation(libs.commons.codec)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.retrofit)
    implementation(libs.jackson.dataformat.cbor)
    implementation(libs.jackson.module.kotlin)

    testImplementation(libs.bundles.junit.test)
    testImplementation(libs.bundles.kotlinx.test)

    implementation(libs.logging.interceptor)
}

publishing {
    repositories {
        maven {
            val properties = Properties().apply {
                file("../local.properties").inputStream().use { load(it) }
            }
            name = "icp-kotlin-kit"
            url = uri("https://maven.pkg.github.com/ThomasConstantinBity/icp-kotlin-kit")
            credentials {
                username = properties["gpr.user"] as String? ?: System.getenv("USERNAME")
                password = properties["gpr.key"] as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            version = "0.0.1"
            groupId = "com.bity"
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