//import org.javamodularity.moduleplugin.extensions.CompileModuleOptions
//import org.javamodularity.moduleplugin.tasks.TestModuleOptions

//region NO-OP (DSL testing)
//tasks.compileJava {
//    extensions.configure<CompileModuleOptions> {
//        addModules = listOf()
//        compileModuleInfoSeparately = false
//    }
//}
//
//tasks.test {
//    extensions.configure<TestModuleOptions> {
//        addModules = listOf()
//        runOnClasspath = false
//    }
//}

//modularity {
//}
//endregion


//val moduleName: String by project
//
plugins {
    application
}
//application {
//    mainClassName = "$moduleName/cos.olympus.Main"
//    applicationDefaultJvmArgs = listOf("-XX:+PrintGCDetails")
//}
//


dependencies {
    implementation("io.vertx:vertx-core:3.8.5")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
}
