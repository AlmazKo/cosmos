import java.lang.String.join

plugins {
    application
}

java.modularity.inferModulePath.set(true)

dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")
    implementation(project(":ops"))
    implementation(project(":map"))
    implementation(project(":logging"))
    implementation("almazko:microjson:0.5")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

val mainClazz = "cos.olympus.Main"
val mainModule = "cos.olympus"

application {
    mainClass.set(mainClazz)

    applicationDefaultJvmArgs = listOf(
        "--enable-preview",
        "-Xmx100m"
        //        "-verbose:class"
        //        "-XX:+UnlockExperimentalVMOptions",
        //        "-XX:+UseEpsilonGC",
    )
}



tasks {
    val imageDir = "image"
    register<Copy>("libs") {
        into("build/libs/")
        from(configurations.compileClasspath)
    }

    register("patchImage") {
        dependsOn("jlink")
        val opts = listOf(
            "--enable-preview",
            "-Duser.timezone=UTC",
            "-Xmx64m",
            "-XX:+CrashOnOutOfMemoryError",
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-XX:HeapDumpPath=/tmp",
           // "-Xlog:gc",
            "-XX:+UseG1GC"
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
        dependsOn("classes", "libs")
        delete(imageDir)

        val javaHome = System.getProperty("java.home")
        val buildDir = "build/classes/java/main/"
        commandLine(
            "$javaHome/bin/jlink",
            "--module-path", join(
                ":",
                "../libs", // contains java9 modules
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

