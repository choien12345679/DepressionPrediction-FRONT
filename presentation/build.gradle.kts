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
    namespace = "com.imp.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")

        // 2. [핵심 수정] 값을 읽어와서 앞뒤에 따옴표(\")를 붙여줍니다.
        val chattingHost = properties.getProperty("chatting_server_host") ?: ""
        buildConfigField("String", "CHATTING_SERVER_HOST", "\"$chattingHost\"")
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
        dataBinding = true
        viewBinding = true
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
    implementation(project(":data"))

    /** Android */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /** Kakao Map */
    implementation(libs.kakao.map)

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

    /** Glide */
    implementation(libs.glide)
    implementation(libs.glide.transformations)

    /** Screen Size */
    implementation(libs.screen.easy)

    /** Splash */
    implementation(libs.androidx.core.splashscreen)

    /** MP Chart */
    implementation(libs.mpandroidchart)

    /** Lottie */
    implementation(libs.lottie)

    /** WorkManager */
    implementation(libs.androidx.work.runtime)

    /**
     * Indicator SeekBar
     */
    implementation("com.github.warkiz:IndicatorSeekBar:v2.1.1")

    /** Indicator */
    implementation(libs.dotsindicator)

    /** Number Picker */
    implementation(libs.number.picker)

    /** AR SceneForm */
    implementation(libs.sceneform)

    /** Balloon */
    implementation(libs.balloon)
}