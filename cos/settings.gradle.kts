rootProject.name = "cos"

include(
    "cos.agent",
    "cos.map",
    "cos.logging",
    "cos.olympus",
    "cos.api",
    "cos.json"
)


pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin-multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}
