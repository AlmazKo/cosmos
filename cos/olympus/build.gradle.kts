plugins {
    application
}

val mainClazz = "cos.olympus.Main"
val mainModule = "cos.olympus"

application {
    mainClass.set(mainClazz)

    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
//        "-verbose:class",
        "--enable-preview",
        "-XX:+UseZGC",
        "-Xmx64m"
        //        "-XX:+UnlockExperimentalVMOptions",
//                "-XX:+UseEpsilonGC"
    )
}

dependencies {
    implementation(files("../mods/annotations-20.1.0.jar"))
    implementation(files("../mods/microjson-0.6.3.jar"))
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":nio"))
    implementation(project(":logging"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}




tasks {
    val imageDir = "image"
    register<Copy>("moveLibs") {
        from(configurations.compileClasspath)
        into("build/libs/")
    }

    register("patchImage") {
        dependsOn("jlink")
        val opts = listOf(
            "-Duser.timezone=UTC",
            "-Xmx64m",
            "-XX:+CrashOnOutOfMemoryError",
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-XX:HeapDumpPath=/dumps",
            //"-verbose:class",
            //"-Xlog:gc",
            "-XX:+UseZGC",
            "\$JAVA_EXTRA_OPTIONS"
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
        dependsOn("assemble", "processResources", "moveLibs")
        delete(imageDir)

        val javaHome = System.getProperty("java.home")
        commandLine(
            "$javaHome/bin/jlink",
            "--module-path", "build/libs/:../mods/",
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

