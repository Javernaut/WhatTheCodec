pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")

            content {
                includeModule("io.github.javernaut", "mediafile")
            }
        }
    }
}

rootProject.name = "WhatTheCodec"
include(":app")
include(":baselineprofile")
include(":features:settings:api")
include(":features:settings:data")
include(":features:settings:ui")
include(":features:home:localization")

//includeBuild("../MediaFile")
