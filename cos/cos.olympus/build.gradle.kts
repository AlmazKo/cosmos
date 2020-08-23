//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
}


java {
    modularity.inferModulePath.set(true)
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("--enable-preview")
}


dependencies {
    compileOnly("org.jetbrains:annotations:20.0.0")
    implementation(project(":cos.ops"))
    implementation(project(":cos.map"))
    implementation(project(":cos.logging"))
    implementation("almazko:microjson:0.5")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

application {
    mainClassName = "cos.olympus.Main"
    applicationDefaultJvmArgs = listOf(
        //        "-Xmx128m",
        //        "-XX:+UnlockExperimentalVMOptions",
        //        "-XX:+UseEpsilonGC",
        //        "-verbose:gc",
        //                        "-verbose:class",
        //                "-javaagent:/Users/aleksandrsuslov/projects/mmo/cos/cos.agent/build/libs/agent.jar",
        "--enable-preview"
    )
}


//tasks.withType<ShadowJar> {
//    archiveFileName.set("olympus.jar")
//    mergeServiceFiles()
//    manifest {
//        attributes(
//            mapOf(
//                "Main-Class" to "$moduleName/cos.olympus.Main",
//                "Specification-Title" to "Olympus core server"
//            )
//        )
//    }
//}

//tasks {
//    register<Exec>("jlink") {
//        group = "Build"
//        description = "Generate custom Java runtime image"
//        dependsOn("classes")
//        delete("image")
//
//        val javaHome = System.getProperty("java.home")
//        val buildDir = "build/classes/java/main/"
//        commandLine(
//            "$javaHome/bin/jlink",
//            "--module-path",
//            "$buildDir:../cos.logging/$buildDir:../cos.map/$buildDir:../cos.json/$buildDir:../libs/:$javaHome/jmods",
//            "--strip-debug",
//            "--no-header-files",
//            "--no-man-pages",
//            "--add-modules", "cos.olympus",
//            "--launcher",
//            "launch=cos.olympus/cos.olympus.Main",
//            "--output", "image"
//        )
//    }
//}
//// '--enable-preview -Xmx32m -verbose:class'
