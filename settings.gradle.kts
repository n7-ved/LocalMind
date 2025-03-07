pluginManagement {
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
    }
}

rootProject.name = "LocalMind"
include(":androidApp")
include(":component:chat")
include(":component:rag")
include(":component:local-llm")
include(":component:remote-gpt")
include(":component:profile")
include(":core:network")
include(":core:storage:database:room")
include(":core:storage:database:objectbox")
include(":core:storage:datastore")
include(":core:local-llm")
include(":core:sentence-embedding-model")
include(":core:inference-engine")
