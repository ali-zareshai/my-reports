pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven { url=uri("https://jitpack.io") }
    }
}

rootProject.name = "My Report"
include(":app")
