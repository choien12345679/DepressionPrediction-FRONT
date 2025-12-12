import java.util.Properties

// 1. local.properties 파일 로드
val properties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

plugins {
    // [중요] androidApplication이 무조건 가장 먼저 와야 합니다!
    alias(libs.plugins.androidApplication)

    // 그 다음 코틀린 관련 플러그인들
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinParcelize)
    alias(libs.plugins.kotlinKapt)

    id("com.google.gms.google-services") version "4.4.2" apply false
    id(libs.plugins.daggerHiltAndroidPlugin.get().pluginId)
}

android {
    namespace = "com.imp.fluffymood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.imp.fluffymood"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // local.properties에서 API 키 읽어오기 (없으면 빈 문자열)
        val kakaoKey = properties.getProperty("KAKAO_API_KEY") ?: ""
        // [중요] 문자열로 넘기기 위해 이스케이프 시퀀스(\") 사용
        buildConfigField("String", "KAKAO_API_KEY", "\"$kakaoKey\"")
        // Manifest에서도 사용할 수 있도록 설정 (선택사항)
        manifestPlaceholders["KAKAO_API_KEY"] = kakaoKey
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "sample-app-v${versionName}.apk"
                // println("OutputFileName: $outputFileName")
                output.outputFileName = outputFileName
            }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isShrinkResources = false
            isMinifyEnabled = false
        }

        release {
            isDebuggable = false
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

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        htmlReport = true
        htmlOutput = file("${project.buildDir}/reports/lint/lint.html")
        disable.add("MissingTranslation")
        disable.add("GradleDependency")
        disable.add("VectorPath")
        disable.add("IconMissingDensityFolder")
        disable.add("IconDensities")
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
    // Multi Module
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))

    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Kakao Map
    implementation(libs.kakao.map)
}
