import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.41" apply false
    id("org.javamodularity.moduleplugin") version "1.5.0" apply false
    id("kotlinx-serialization") version "1.3.40" apply false
//    id("kotlin-multiplatform") version "1.3.30" apply false
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
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
    apply(plugin = "kotlin")
    apply(plugin = "org.javamodularity.moduleplugin")

    //region https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
    val test by tasks.existing(Test::class)
    val build by tasks
    val javadoc by tasks

    val implementation by configurations
    val testImplementation by configurations
    val testRuntimeOnly by configurations

    val jUnitVersion: String by project
    //endregion

    //region KOTLIN
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "12"
        kotlinOptions.noReflect = true
        kotlinOptions.languageVersion = "1.3"
        kotlinOptions.apiVersion = "1.3"
        kotlinOptions.suppressWarnings = true
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("com.google.flogger:flogger:0.4")
        implementation("com.google.flogger:flogger-system-backend:0.4")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
        compile("org.jetbrains:annotations:17.0.0")
    }
    //endregion

    repositories {
        mavenCentral()
    }
}
