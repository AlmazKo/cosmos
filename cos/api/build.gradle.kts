plugins {
    java
    application
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.10"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.apply {
        jvmTarget = "15"
        noReflect = true
        noStdlib = true
        noJdk = false
        noReflect = true
        includeRuntime = false
        languageVersion = "1.4"
        apiVersion = "1.4"
        suppressWarnings = true
    }
}
dependencies {
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":logging"))
    implementation("io.vertx:vertx-core:3.9.4")
    implementation("io.vertx:vertx-web:3.9.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
}



application {
    mainClass.set("cos.api.Main")
    applicationDefaultJvmArgs = listOf(
        "--enable-preview",
        "-Xmx200m"
//        "-verbose:class"
    )
}
