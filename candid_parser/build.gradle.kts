import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("com.gradle.plugin-publish") version "1.2.1"
    alias(libs.plugins.jetbrainsKotlinJvm)
    `java-gradle-plugin`
    `kotlin-dsl`
    // `maven-publish`
}

publishing {
    publications {
        withType<MavenPublication>().configureEach {
            groupId = "com.bity"
            version = "1.0.0"
        }
    }
    repositories {
        maven {
            url = uri("./build/myCustomFolder")
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(kotlin("stdlib"))

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation(libs.bundles.junit.test)
    testImplementation(libs.bundles.kotlinx.test)}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}