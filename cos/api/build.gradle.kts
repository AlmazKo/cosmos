//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
//    id("com.github.johnrengelman.shadow") version "7.0.0"
}

dependencies {
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":logging"))
    implementation(project(":nio"))
    implementation(project(":olympus"))
    implementation("io.vertx:vertx-core:4.4.5")
    implementation("io.vertx:vertx-web:4.4.5")
    implementation(files("../mods/annotations-20.1.0.jar"))
}



application {
//    mainClassName = "cos.api.Main" // need for ShadowJar
    mainClass.set("cos.api.Main")
    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
        "-DCosOlympusHost=localhost",
        "-DCosResourcesDir=../../resources",
        "--enable-preview",
        "-XX:+UseZGC",
        "-Xmx256m"
//        "-verbose:class"
    )
}



tasks {

    withType<JavaCompile> {
        options.compilerArgs = listOf("--enable-preview")
    }

//    withType<ShadowJar> {
//        archiveFileName.set("api.jar")
//    }
//
//    withType<com.github.jengelman.gradle.plugins.shadow.internal.JavaJarExec> {
//        args = listOf("--enable-preview")
//    }
}
