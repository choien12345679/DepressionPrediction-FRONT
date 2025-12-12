import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    id(libs.plugins.daggerHiltAndroidPlugin.get().pluginId)
}

// 1. local.properties 파일 안전하게 불러오기
val properties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.imp.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")

        // 2. [핵심 수정] 값을 읽어와서 앞뒤에 따옴표(\")를 붙여줍니다.
        // 이렇게 해야 자바 코드에서 String으로 정상 인식됩니다.

        val kakaoKey = properties.getProperty("kakao_rest_api_key") ?: ""
        buildConfigField("String", "KAKAO_REST_API_KEY", "\"$kakaoKey\"")

        val serviceHost = properties.getProperty("service_server_host") ?: ""
        buildConfigField("String", "SERVICE_SERVER_HOST", "\"$serviceHost\"")

        val devHost = properties.getProperty("dev_server_host") ?: ""
        buildConfigField("String", "DEV_SERVER_HOST", "\"$devHost\"")

        val chatHost = properties.getProperty("chat_server_host") ?: ""
        buildConfigField("String", "CHAT_SERVER_HOST", "\"$chatHost\"")
    }

    buildTypes {
        debug {
            isShrinkResources = false
            isMinifyEnabled = false
        }

        release {
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/notice.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/services/javax.annotation.processing.Processor")
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }
}

dependencies {
    /** Multi Module */
    implementation(project(":domain"))

    /** Android */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /** Hilt */
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    /** lifeCycle */
    implementation(libs.bundles.lifecycle)

    /** RxJava3 */
    implementation(libs.bundles.rxjava3)

    /** RxJava2 */
    implementation(libs.bundles.rxjava2)

    /** Coroutine Core */
    implementation(libs.bundles.coroutine)

    /** Retrofit */
    implementation(libs.bundles.retrofit)

    /** OkHttp */
    implementation(libs.bundles.okhttp)

    /** moshi */
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin.codegen)

    /** Gson */
    implementation(libs.gson)

    /** WorkManager */
    implementation(libs.androidx.work.runtime)

    /** DataStore */
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.datastore.preferences.core)

    /** GPS */
    implementation(libs.play.services.location)

    /** ML - Smile */
    implementation(libs.smile.core)
    implementation(libs.smile.data)
}
