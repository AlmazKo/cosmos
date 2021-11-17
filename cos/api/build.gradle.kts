import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":logging"))
    implementation(project(":nio"))
    implementation("io.vertx:vertx-core:3.9.4")
    implementation("io.vertx:vertx-web:3.9.4")
    implementation(files("../mods/annotations-20.1.0.jar"))
}



application {
    mainClassName = "cos.api.Main" // need for ShadowJar
    mainClass.set("cos.api.Main")
    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
        "--enable-preview",
        "-XX:+UseZGC",
        "-Xmx128m"
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
}
