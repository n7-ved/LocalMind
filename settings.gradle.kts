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
include(":component:local-llm")
include(":component:remote-gpt")
include(":core:network")
include(":core:local-llm")
include(":core:sentence-embedding-model")
include(":core:inference-engine")
include(":component:local-rag")
include(":component:remote-claude")
include(":component:remote-gemini")
include(":core:local-db")
include(":core:cache")
include(":core:openai-client")
include(":component:common")
include(":core:vector-db")
include(":feature:document-local-rag")
include(":core:design-system")
include(":feature:main")
include(":feature:remote-gpt")
include(":feature:onboarding")
include(":feature:performance-local-rag")
include(":core:common")
