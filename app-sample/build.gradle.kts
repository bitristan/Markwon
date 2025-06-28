import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

// Git SHA获取函数
val gitSha by lazy {
    val output = ByteArrayOutputStream()
    project.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = output
    }
    output.toString().trim()
}

android {
    namespace = "io.noties.markwon.app"
    compileSdk = (rootProject.extra["config"] as Map<String, Any>)["compile-sdk"] as Int
    buildToolsVersion = (rootProject.extra["config"] as Map<String, Any>)["build-tools"] as String

    defaultConfig {
        applicationId = "io.noties.markwon.app"
        minSdk = 23
        targetSdk = (rootProject.extra["config"] as Map<String, Any>)["target-sdk"] as Int
        versionCode = 1
        versionName = project.version.toString()

        resourceConfigurations.addAll(listOf("en"))

        setProperty("archivesBaseName", "markwon")

        buildConfigField("String", "GIT_SHA", "\"$gitSha\"")
        buildConfigField("String", "GIT_REPOSITORY", "\"https://github.com/noties/Markwon\"")

        val scheme = "markwon"
        buildConfigField("String", "DEEPLINK_SCHEME", "\"$scheme\"")
        manifestPlaceholders["deeplink_scheme"] = scheme
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("../sample-utils/annotations")
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        debug {
            // Debug版本不需要签名
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // 仅在非CI环境且有签名配置时启用签名
    if (!project.hasProperty("CI")) {
        val keystoreFile = project.file("keystore.jks")
        val keystoreFilePassword = "MARKWON_KEYSTORE_FILE_PASSWORD"
        val keystoreAlias = "MARKWON_KEY_ALIAS"
        val keystoreAliasPassword = "MARKWON_KEY_ALIAS_PASSWORD"

        val properties = listOf(
            keystoreFilePassword,
            keystoreAlias,
            keystoreAliasPassword
        )

        val hasSigningProperties = properties.all { project.hasProperty(it) }
        
        if (keystoreFile.exists() && hasSigningProperties) {
            signingConfigs {
                create("config") {
                    storeFile = keystoreFile
                    storePassword = project.property(keystoreFilePassword) as String
                    keyAlias = project.property(keystoreAlias) as String
                    keyPassword = project.property(keystoreAliasPassword) as String
                }
            }

            buildTypes {
                debug {
                    signingConfig = signingConfigs.getByName("config")
                }
                release {
                    signingConfig = signingConfigs.getByName("config")
                }
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    arguments {
        arg("markwon.samples.file", "${projectDir}/samples.json")
    }
}

configurations.all {
    exclude(group = "org.jetbrains", module = "annotations-java5")
}

dependencies {
    val deps = rootProject.extra["deps"] as Map<String, String>
    val testDeps = rootProject.extra["testDeps"] as Map<String, String>
    val annotationProcessorDeps = rootProject.extra["annotationProcessor"] as Map<String, String>
    val kotlinVersion = rootProject.extra["kotlin_version"] as String

    kapt(project(":sample-utils:processor"))
    kapt(annotationProcessorDeps["prism4j-bundler"]!!)

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // Markwon modules
    implementation(project(":markwon-core"))
    implementation(project(":markwon-editor"))
    implementation(project(":markwon-ext-latex"))
    implementation(project(":markwon-ext-strikethrough"))
    implementation(project(":markwon-ext-tables"))
    implementation(project(":markwon-ext-tasklist"))
    implementation(project(":markwon-html"))
    implementation(project(":markwon-image"))
    implementation(project(":markwon-inline-parser"))
    implementation(project(":markwon-linkify"))
    implementation(project(":markwon-recycler"))
    implementation(project(":markwon-recycler-table"))
    implementation(project(":markwon-simple-ext"))
    implementation(project(":markwon-syntax-highlight"))

    implementation(project(":markwon-image-picasso"))
    implementation(project(":markwon-image-glide"))
    implementation(project(":markwon-image-coil"))

    // External dependencies
    implementation(deps["x-appcompat"]!!)
    implementation(deps["x-recycler-view"]!!)
    implementation(deps["x-cardview"]!!)
    implementation(deps["x-fragment"]!!)
    implementation(deps["okhttp"]!!)
    implementation(deps["prism4j"]!!)
    implementation(deps["gson"]!!)
    implementation(deps["adapt"]!!)
    implementation(deps["debug"]!!)
    implementation(deps["android-svg"]!!)
    implementation(deps["android-gif-impl"]!!)
    implementation(deps["coil"]!!)

    // Test dependencies
    testImplementation(testDeps["junit"]!!)
    testImplementation(testDeps["robolectric"]!!)
    testImplementation(testDeps["mockito"]!!)
} 