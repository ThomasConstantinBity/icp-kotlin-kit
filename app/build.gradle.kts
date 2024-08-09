import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    id("com.github.willir.rust.cargo-ndk-android")
}

// apply(plugin = "com.bity.candid_parser")
buildscript {
    dependencies {
        classpath(files("../candid_parser/build/myCustomFolder/com/bity/candid_parser/1.0.0/candid_parser-1.0.0.jar"))
    }
}

android {
    namespace = "com.bity.icpkotlinkit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bity.icpkotlinkit"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties().apply {
            file("../local.properties").inputStream().use { load(it) }
        }
        buildConfigField(
            "String",
            "ICP_PUB_KEY",
            properties["ICP_PUB_KEY"].toString()
        )
        buildConfigField(
            "String",
            "ICP_PRIV_KEY",
            properties["ICP_PRIV_KEY"].toString()
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

cargoNdk {
    targets = arrayListOf("arm64", "arm", "x86", "x86_64")
    module = "icp_kotlin_kit/rust/bls12381"
    targetDirectory = "target"
    buildType = "release"
    // extraCargoEnv = ["CARGO_TARGET_DIR": System.getProperty("user.dir") + "/rust/bls12381/target/"]
    verbose = false
}

dependencies {
    implementation(project(":icp_kotlin_kit"))

    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation(libs.tooling.preview)

    implementation(libs.androidx.core.ktx)
    implementation(libs.livedata.runtime)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.compose.material)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.bundles.koin)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.foundation.layout.android)
}