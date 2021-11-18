import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":logging"))
    implementation(project(":nio"))
    implementation("io.vertx:vertx-core:4.2.1")
    implementation("io.vertx:vertx-web:4.2.1")
    implementation(files("../mods/annotations-20.1.0.jar"))
}



application {
    mainClassName = "cos.api.Main" // need for ShadowJar
    mainClass.set("cos.api.Main")
    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
        "-DCosOlympusHost=localhost",
        "--enable-preview",
        "-XX:+UseZGC",
        "-Xmx128m"
//        "-verbose:class"
    )
}



tasks {

    withType<JavaCompile> {
        options.compilerArgs = listOf("--enable-preview")
    }

    withType<ShadowJar> {
        archiveFileName.set("api.jar")
    }

    withType<com.github.jengelman.gradle.plugins.shadow.internal.JavaJarExec> {
        args = listOf("--enable-preview")
    }
}
