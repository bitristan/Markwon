// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.gradle.api.tasks.testing.Test

plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.github.ben-manes.versions") version "0.50.0" apply false
}

buildscript {
    extra.apply {
        set("kotlin_version", "2.0.0")
    }
}

allprojects {
    repositories {
        if (project.hasProperty("LOCAL_MAVEN_URL")) {
            maven { url = uri(project.property("LOCAL_MAVEN_URL") as String) }
        }
        google()
        mavenCentral()
//        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
    version = project.property("VERSION_NAME") as String
    group = project.property("GROUP") as String

    // 禁用javadoc生成
    tasks.withType<Javadoc> {
        enabled = false
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.wrapper {
    gradleVersion = "8.5"
    distributionType = Wrapper.DistributionType.ALL
}

// 本地发布配置
if (hasProperty("local")) {
    if (!hasProperty("LOCAL_MAVEN_URL")) {
        throw RuntimeException("Cannot publish to local maven as no such property exists: LOCAL_MAVEN_URL")
    }
    extra["RELEASE_REPOSITORY_URL"] = property("LOCAL_MAVEN_URL")
    extra["SNAPSHOT_REPOSITORY_URL"] = property("LOCAL_MAVEN_URL")
}

// 全局配置
extra.apply {
    val config = mapOf(
        "build-tools" to "34.0.0",
        "compile-sdk" to 34,
        "target-sdk" to 34,
        "min-sdk" to 21, // 提升最小API级别
        "push-aar-gradle" to "https://raw.githubusercontent.com/noties/gradle-mvn-push/master/gradle-mvn-push-aar.gradle"
    )
    set("config", config)

    val commonMarkVersion = "0.13.0"
    val daggerVersion = "2.48.1"
    val coilVersion = "2.5.0"

    val deps = mapOf(
        // AndroidX 依赖
        "x-annotations" to "androidx.annotation:annotation:1.7.1",
        "x-recycler-view" to "androidx.recyclerview:recyclerview:1.3.2",
        "x-core" to "androidx.core:core:1.12.0",
        "x-appcompat" to "androidx.appcompat:appcompat:1.6.1",
        "x-cardview" to "androidx.cardview:cardview:1.0.0",
        "x-fragment" to "androidx.fragment:fragment:1.6.2",
        
        // CommonMark 依赖
        "commonmark" to "com.atlassian.commonmark:commonmark:$commonMarkVersion",
        "commonmark-strikethrough" to "com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:$commonMarkVersion",
        "commonmark-table" to "com.atlassian.commonmark:commonmark-ext-gfm-tables:$commonMarkVersion",
        
        // 其他依赖
        "android-svg" to "com.caverock:androidsvg:1.4",
        "android-gif" to "pl.droidsonroids.gif:android-gif-drawable:1.2.25",
        "android-gif-impl" to "pl.droidsonroids.gif:android-gif-drawable:1.2.25",
        "jlatexmath-android" to "ru.noties:jlatexmath-android:0.2.0",
        "okhttp" to "com.squareup.okhttp3:okhttp:4.12.0",
        "prism4j" to "io.noties:prism4j:2.0.0",
        "debug" to "io.noties:debug:5.1.0",
        "adapt" to "io.noties:adapt:2.2.0",
        "dagger" to "com.google.dagger:dagger:$daggerVersion",
        "picasso" to "com.squareup.picasso:picasso:2.8",
        "glide" to "com.github.bumptech.glide:glide:4.16.0",
        "coil" to "io.coil-kt:coil:$coilVersion",
        "coil-base" to "io.coil-kt:coil-base:$coilVersion",
        "ix-java" to "com.github.akarnokd:ixjava:1.0.0",
        "gson" to "com.google.code.gson:gson:2.10.1",
        "commons-io" to "commons-io:commons-io:2.15.1"
    )
    set("deps", deps)

    val annotationProcessorDeps = mapOf(
        "prism4j-bundler" to "io.noties:prism4j-bundler:2.0.0",
        "dagger-compiler" to "com.google.dagger:dagger-compiler:$daggerVersion"
    )
    set("annotationProcessor", annotationProcessorDeps)

    val testDeps = mapOf(
        "junit" to "junit:junit:4.13.2",
        "robolectric" to "org.robolectric:robolectric:4.11.1",
        "mockito" to "org.mockito:mockito-core:5.8.0",
        "commonmark-test-util" to "com.atlassian.commonmark:commonmark-test-util:$commonMarkVersion"
    )
    set("testDeps", testDeps)
}

// 检查更新任务
tasks.register("checkUpdates") {
    apply(plugin = "com.github.ben-manes.versions")
    dependsOn("dependencyUpdates")
}

// 注册构件的函数
fun registerArtifact(project: Project) {
    if (hasProperty("release")) {
        // 用于GitHub Actions（发布快照）
        // 但仅在版本名称包含SNAPSHOT时
        if (hasProperty("CI") && (property("VERSION_NAME") as String).contains("SNAPSHOT")) {
            extra["NEXUS_USERNAME"] = System.getenv("NEXUS_USERNAME")
            extra["NEXUS_PASSWORD"] = System.getenv("NEXUS_PASSWORD")
        }

        project.apply(from = (extra["config"] as Map<*, *>)["push-aar-gradle"])
    }

    project.afterEvaluate {
        // 禁用BuildConfig文件生成
        project.extensions.findByType<com.android.build.gradle.LibraryExtension>()?.let { android ->
            try {
                android.buildFeatures.buildConfig = false
            } catch (e: Exception) {
                // 如果设置失败，忽略错误
            }
        }

        // 打印测试状态（用于CI）
        project.tasks.withType<Test> {
            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
                showStandardStreams = true
            }
        }
    }
}

// 将函数添加到extra properties
extra["registerArtifact"] = ::registerArtifact 