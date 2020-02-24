//import org.javamodularity.moduleplugin.tasks.ModuleOptions

plugins {
    application
}

//region https://docs.gradle.org/current/userguide/kotlin_dsl.html#using_kotlin_delegated_properties
//val moduleName: String by project
val run by tasks.existing(JavaExec::class) // https://youtrack.jetbrains.com/issue/KT-28013
//endregion

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation(project(":cos.map"))
    implementation("io.vertx:vertx-core:3.8.5")
    implementation("io.vertx:vertx-web:3.8.5")
}


application {
    mainClassName = "cos.api.Main"
//    applicationDefaultJvmArgs = listOf("-XX:+PrintGCDetails")
}

//patchModules.config = listOf(
//    "java.annotation=jsr305-3.0.2.jar"
//)
//
//(run) {
////    extensions.configure<ModuleOptions> {
////        addModules = listOf("java.sql")
////    }
////
////    jvmArgs = listOf("-XX:+PrintGCDetails")
//}
