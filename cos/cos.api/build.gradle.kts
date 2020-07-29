//import org.javamodularity.moduleplugin.tasks.ModuleOptions

plugins {
    application
    kotlin("jvm") version "1.3.72" apply false
    id("kotlinx-serialization") version "1.3.72" apply false
}

apply(plugin = "kotlin")
apply(plugin = "kotlinx-serialization")


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.apply {
        jvmTarget = "12"
        noReflect = true
        noStdlib = true
        noJdk = false
        noReflect = true
        includeRuntime = false
        languageVersion = "1.3"
        apiVersion = "1.3"
        suppressWarnings = true
    }
}
//region https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
//val moduleName: String by project
//val run by tasks.existing(JavaExec::class) // https://youtrack.jetbrains.com/issue/KT-28013
//endregion

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation(project(":cos.map"))
    implementation(project(":cos.logging"))
    implementation("io.vertx:vertx-core:3.8.5")
    implementation("io.vertx:vertx-web:3.8.5")
}


application {
    mainClassName = "cos.api.Main"
    applicationDefaultJvmArgs = listOf(
        //        "-verbose:gc",
        //                "-verbose:class",
        "--enable-preview",
        "-Xmx128m"
    )
}

//patchModules.config = listOf(
//    "java.annotation=jsr305-3.0.2.jar"
//)
//
//(run) {
//    //    extensions.configure<ModuleOptions> {
//    //        addModules = listOf("java.sql")
//    //    }
//    //
//    //    jvmArgs = listOf("-XX:+PrintGCDetails")
//}
