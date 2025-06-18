import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    load(File(rootDir, "local.properties").inputStream())
}

val openAiApiKeyAndroidApp: String = localProperties.getProperty("LOCAL_PROPERTIES_OPENAI_API_KEY") ?: ""


android {
    namespace = "com.n7.localmind"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.n7.localmind"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPENAI_API_KEY_AndroidApp", "\"$openAiApiKeyAndroidApp\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {

    implementation(project(":core:cache"))
    implementation(project(":core:local-db"))
    implementation(project(":core:vector-db"))
    implementation(project(":core:local-llm"))
    implementation(project(":core:network"))
    implementation(project(":core:sentence-embedding-model"))
    implementation(project(":core:design-system"))
    implementation(project(":component:chat"))
    implementation(project(":component:common"))
    implementation(project(":component:local-llm"))
    implementation(project(":component:local-rag"))
    implementation(project(":component:remote-gpt"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:main"))
    implementation(project(":feature:document-local-rag"))
    implementation(project(":feature:performance-local-rag"))
    implementation(project(":feature:remote-gpt"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.ktor.bom))

    implementation(libs.ktor.client.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.navigation.compose)
}