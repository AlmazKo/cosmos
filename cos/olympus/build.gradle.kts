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

application {
    mainClass.set("cos.olympus.Main")

    applicationDefaultJvmArgs = listOf(
        "--enable-preview",
        "-Xmx100m"
        //        "-verbose:class"
        //        "-XX:+UnlockExperimentalVMOptions",
        //        "-XX:+UseEpsilonGC",
    )
}
