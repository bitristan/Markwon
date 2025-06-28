plugins {
    id("com.android.library")
}

android {
    namespace = "io.noties.markwon.ext.tasklist"
    compileSdk = (rootProject.extra["config"] as Map<String, Any>)["compile-sdk"] as Int
    buildToolsVersion = (rootProject.extra["config"] as Map<String, Any>)["build-tools"] as String

    defaultConfig {
        minSdk = (rootProject.extra["config"] as Map<String, Any>)["min-sdk"] as Int
        targetSdk = (rootProject.extra["config"] as Map<String, Any>)["target-sdk"] as Int
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val deps = rootProject.extra["deps"] as Map<String, String>
    val testDeps = rootProject.extra["testDeps"] as Map<String, String>

    api(project(":markwon-core"))

    testImplementation(project(":markwon-test-span"))
    testImplementation(deps["commons-io"]!!)

    testImplementation(testDeps["junit"]!!)
    testImplementation(testDeps["robolectric"]!!)
}

// 应用注册构件函数
(rootProject.extra["registerArtifact"] as (Project) -> Unit)(project) 