plugins {
    java
    application
    `java-library`
}

group = "cos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("mods/annotations-20.1.0.jar"))
    implementation(files("mods/microjson-0.6.3.jar"))
    implementation("io.vertx:vertx-core:4.5.7")
    implementation("io.vertx:vertx-web:4.5.7")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
}

application {
//    mainClassName = "cos.api.Main" // need for ShadowJar
    mainClass.set("cos.api.Main")
    applicationDefaultJvmArgs = listOf(
        "-DFxTraceLogs=true",
        "-DCosOlympusHost=localhost",
        "-DCosResourcesDir=../resources",
        "--enable-preview",
        "-XX:+UseZGC",
        "-Xmx256m"
//        "-verbose:class"
    )
}


configure<JavaPluginExtension> {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of("21"))
    }

    modularity.inferModulePath.set(true)
}


tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("--enable-preview")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
