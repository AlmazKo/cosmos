import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
}

val moduleName: String by project
//val run by tasks.existing(JavaExec::class) // https://youtrack.jetbrains.com/issue/KT-28013

dependencies {
    implementation("org.jetbrains:annotations:18.0.0")
    implementation(project(":cos.map"))
    implementation(project(":cos.logging"))
}


application {

    mainClassName = "$moduleName/cos.olympus.Main"
    applicationDefaultJvmArgs = listOf(
        //        "-verbose:gc",
//                "-verbose:class",
        "--enable-preview",
        "-Xmx128m"
    )
}


tasks.withType<ShadowJar> {
    archiveFileName.set("olympus.jar")
    mergeServiceFiles()
    minimize()
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName,
                "Specification-Title" to "Olympus core server"
            )
        )
    }
}





tasks {
    //    jar {
    //        manifest {
    //            attributes(
    //                "Main-Class" to "org.rognan.Application"
    //            )
    //        }
    //    }

    register<Exec>("jlink") {
        group = "Build"
        description = "Generate custom Java runtime image"

        dependsOn("clean", "jar")

        val javaHome = System.getProperty("java.home")
        //        val moduleName = "org.rognan.jlink"
        //        val moduleLaunchPoint = "org.rognan.Application"

        workingDir = file("build")

        commandLine(
            "$javaHome/bin/jlink",
            "--module-path", "libs${File.pathSeparatorChar}$javaHome/jmods",
            "--module-path", "../cos.map/build/libs${File.pathSeparatorChar}$javaHome/jmods",
            "--strip-debug", "--no-header-files", "--no-man-pages", "--compress", "2",
            "--add-modules", "cos.olympus, cos.map",
            "--launcher", "launch=${application.mainClassName}",
            "--output", "image"
        )
    }
}
