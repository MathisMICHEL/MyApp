pluginManagement {
    repositories {
        google()  // Google's Maven repository for Android dependencies
        mavenCentral()  // Central repository for many dependencies
        gradlePluginPortal()  // Gradle Plugin Portal for Gradle-related plugins
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Ensures no project-specific repositories override global ones
    repositories {
        google()  // Google's Maven repository for Android dependencies
        mavenCentral()  // Central repository for TensorFlow Lite and other dependencies
    }
}

rootProject.name = "MyApp"
include(":app")
