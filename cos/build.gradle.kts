allprojects {
    group = "cos"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.gradle.java")
    apply(plugin = "org.gradle.java-library")

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
}
