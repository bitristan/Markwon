rootProject.name = "MarkwonProject"

include(
    ":markwon-core",
    ":markwon-editor",
    ":markwon-ext-latex",
    ":markwon-ext-strikethrough", 
    ":markwon-ext-tables",
    ":markwon-ext-tasklist",
    ":markwon-html",
    ":markwon-image",
    ":markwon-image-coil",
    ":markwon-image-glide",
    ":markwon-image-picasso",
    ":markwon-inline-parser",
    ":markwon-linkify",
    ":markwon-recycler",
    ":markwon-recycler-table",
    ":markwon-simple-ext",
    ":markwon-syntax-highlight",
    ":markwon-test-span"
)

include(":app-sample")
include(":sample-utils:processor")

// 启用类型安全的项目访问器
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// 配置插件管理
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// 配置依赖解析管理
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
} 