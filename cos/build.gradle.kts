//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    //    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    jcenter()
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

java {
    modularity.inferModulePath.set(true)
}

allprojects {
    group = "cos"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        jcenter()
    }
}

subprojects {

    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://dl.bintray.com/almazko/micro")
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs = listOf("--enable-preview")
    }
}
