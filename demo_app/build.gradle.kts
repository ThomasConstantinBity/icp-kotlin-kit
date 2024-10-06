import com.bity.icp_kotlin_kit.file_parser.file_generator.KotlinFileGenerator

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.bity.demo_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bity.demo_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":icp_kotlin_kit"))
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.koin.androidx.compose)
}

tasks.register("parseCandidFiles") {
    val inputFolder = file("./candid_files")
    require(inputFolder.isDirectory)
    inputFolder.listFiles { it -> it.extension == "did" }?.forEach { file ->
        val fileName = file.name.removeSuffix(".did")
        val kotlinFileGenerator = KotlinFileGenerator(
            fileName = fileName,
            packageName = "com.bity.demo_app.generated_files",
            didFileContent = file.readText(Charsets.UTF_8)
        )
        val outputFile = file("./src/main/java/com/bity/demo_app/generated_files/${fileName}.kt")
        outputFile.writeText(kotlinFileGenerator.generateKotlinFile())
    }
}