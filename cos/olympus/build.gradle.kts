import java.lang.String.join

plugins {
    application
}

java.modularity.inferModulePath.set(true)

dependencies {
    implementation(files("../mods/annotations-20.1.0.jar"))
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":nio"))
    implementation(project(":logging"))
    implementation(files("../mods/microjson-0.6.3.jar"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

val mainClazz = "cos.olympus.Main"
val mainModule = "cos.olympus"

application {
    mainClass.set(mainClazz)

    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
//        "-verbose:class",
        "--enable-preview",
        "-Xmx100m"
        //        "-verbose:class"
        //        "-XX:+UnlockExperimentalVMOptions",
//                "-XX:+UseEpsilonGC"
    )
}



tasks {
    val imageDir = "image"
    register<Copy>("moveLibs") {
        into("build/libs/")
        from(configurations.compileClasspath)
    }

    register("patchImage") {
        dependsOn("jlink")
        val opts = listOf(
//            "-verbose:class",
            "--enable-preview",
            "-Duser.timezone=UTC",
            "-Xmx64m",
            "-XX:+CrashOnOutOfMemoryError",
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-XX:HeapDumpPath=/tmp"
           // "-Xlog:gc",
//            "-XX:+UseZGC"
        ).joinToString(" ")

        doLast {
            val launcher = file("$imageDir/bin/launch")
            val lines = launcher.readLines().map {
                if (it.startsWith("JLINK_VM_OPTIONS")) {
                    "$it\"$opts\""
                } else it
            }
            launcher.writeText(lines.joinToString("\n"))
        }
    }


    register<Exec>("jlink") {
        group = "Build"
        description = "Generate runtime image"
        dependsOn("assemble", "moveLibs")
        delete(imageDir)

        val javaHome = System.getProperty("java.home")
        val buildDir = "build/classes/java/main/"
        commandLine(
            "$javaHome/bin/jlink",
            "--module-path", join(
                ":",
                "../mods", // contains java9 modules
                "build/libs/",
                "../ops/$buildDir",
                "../logging/$buildDir",
                "../map/$buildDir",
                "../json/$buildDir",
                "$javaHome/jmods"
            ),
            "--strip-debug",
            "--no-header-files",
            "--no-man-pages",
            "--vm", "server",
            "--add-modules", mainModule,
            "--launcher", "launch=$mainModule/$mainClazz",
            "--output", imageDir
        )
    }

    register("image") {
        group = "Build"
        dependsOn("jlink", "patchImage")
    }

    register<Exec>("runImage") {
        dependsOn("image")
        group = "Application"
        description = "Run image"
        commandLine("$imageDir/bin/launch")
    }
}

