plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    main {
        java {
            srcDirs("../annotations")
        }
    }
}

dependencies {
    val deps = rootProject.extra["deps"] as Map<String, String>
    
    implementation(deps["x-annotations"]!!)
    implementation(deps["gson"]!!)
    implementation(deps["commons-io"]!!)
} 