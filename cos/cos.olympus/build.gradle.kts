import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
}

val moduleName: String by project
//val run by tasks.existing(JavaExec::class) // https://youtrack.jetbrains.com/issue/KT-28013

dependencies {
//    implementation("org.jetbrains:annotations:18.0.0")
    implementation(project(":cos.json"))
    implementation(project(":cos.map"))
    implementation(project(":cos.logging"))
}


application {

    mainClassName = "$moduleName/cos.olympus.Main"
    applicationDefaultJvmArgs = listOf(
//                "-verbose:gc",
//                "-verbose:class",
//                "-javaagent:/Users/aleksandrsuslov/projects/mmo/cos/cos.agent/build/libs/agent.jar",
        "--enable-preview",
        "-Xmx8m"
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

        dependsOn("classes")

        val javaHome = System.getProperty("java.home")
        //        val moduleName = "org.rognan.jlink"
        //        val moduleLaunchPoint = "org.rognan.Application"

//        workingDir = file("build")


        commandLine(
            "$javaHome/bin/jlink",
//            "--help"
//            "/Library/Java/JavaVirtualMachines/jdk-13.0.2.jdk/Contents/Home/bin/jlink",
            "--module-path", "build/classes/java/main/:../cos.logging/build/classes/java/main/:../cos.map/build/classes/java/main/:../cos.json/build/classes/java/main/:../libs/:$javaHome/jmods",
//            "--module-path", "/Library/Java/JavaVirtualMachines/jdk-13.0.2.jdk/Contents/Home/jmods",
//            "--module-path", "/Users/aleksandrsuslov/projects/mmo/cos/cos.olympus/build/classes/java/main",
//            "--module-path", "/Users/aleksandrsuslov/projects/mmo/cos/cos.logging/build/classes/java/main",
//            "--module-path", "/Users/aleksandrsuslov/projects/mmo/cos/cos.map/build/classes/java/main",
//            "--strip-debug", "--no-header-files", "--no-man-pages",
            "--add-modules", "cos.olympus",
////            "--launcher", "launch=${application.mainClassName}",
            "--launcher", "launch=cos.olympus/cos.olympus.Main",
            "--output", "image5"
        )
    }
}
// '--enable-preview -Xmx32m -verbose:class'
