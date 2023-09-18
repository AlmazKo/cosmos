plugins {
    application
}

val mainClazz = "cos.olympus.Main"
val mainModule = "cos.olympus"

application {
    mainClass.set(mainClazz)

    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
        "-DCosResourcesDir=../../resources",
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

