sourceSets.create("jmh") {
    java.setSrcDirs(listOf("src/jmh/java"))
}

dependencies {
    //    implementation("io.vertx:vertx-core:3.8.5")
    "jmhImplementation"(project)
    "jmhImplementation"("org.openjdk.jmh:jmh-core:1.23")
    "jmhImplementation"("io.vertx:vertx-core:3.8.5")
    "jmhAnnotationProcessor"("org.openjdk.jmh:jmh-generator-annprocess:1.23")
    //    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
}

tasks {
    register("jmh", type = JavaExec::class) {
        dependsOn("jmhClasses")
        group = "benchmark"
        main = "org.openjdk.jmh.Main"
        classpath = sourceSets["jmh"].runtimeClasspath
        //        args(listOf("-h"))
        args("-wi", "1", "-wf", "1", "-i", "1", "-f", "1")
        jvmArgs("--enable-preview")
        //         args(listOf("--enable-preview"))
    }
}
