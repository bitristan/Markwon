plugins {
    id("com.android.library")
}

android {
    namespace = "io.noties.markwon.recycler"
    compileSdk = (rootProject.extra["config"] as Map<String, Any>)["compile-sdk"] as Int
    buildToolsVersion = (rootProject.extra["config"] as Map<String, Any>)["build-tools"] as String

    defaultConfig {
        minSdk = (rootProject.extra["config"] as Map<String, Any>)["min-sdk"] as Int
        
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

    api(project(":markwon-core"))
    api(deps["x-recycler-view"]!!)
}

// 应用注册构件函数
(rootProject.extra["registerArtifact"] as (Project) -> Unit)(project) 