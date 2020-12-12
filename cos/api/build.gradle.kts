import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
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
    mainClassName = "cos.api.Main" // need for ShadowJar
    mainClass.set("cos.api.Main")
    applicationDefaultJvmArgs = listOf(
        "--enable-preview",
        "-Xmx200m"
//        "-verbose:class"
    )
}



tasks {

    withType<ShadowJar> {
        archiveFileName.set("api.jar")
    }

    withType<com.github.jengelman.gradle.plugins.shadow.internal.JavaJarExec> {
        args = listOf("--enable-preview")
    }

    register<JavaExec>("runJar") {
        dependsOn("shadowJar")
        group = "Application"
        description = "Run image"
        main = "-jar /Users/aleksandrsuslov/projects/cosmos/cos/api/build/libs/api.jar";
    }
}
