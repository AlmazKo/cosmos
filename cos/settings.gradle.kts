rootProject.name = "cos"

include(
    "cos.agent",
    "cos.map",
    "cos.logging",
    "cos.ops",
    "cos.olympus",
    "cos.api"
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
