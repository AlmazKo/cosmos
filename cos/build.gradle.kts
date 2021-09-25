allprojects {
    group = "cos"
    version = "1.0-SNAPSHOT"
    repositories {
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        jcenter()
    }
}

subprojects {
    apply(plugin = "org.gradle.java")
    apply(plugin = "org.gradle.java-library")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of("16"))
        }

    }

    repositories {
        maven(url = "https://dl.bintray.com/almazko/micro")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs = listOf("--enable-preview")
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs = listOf("--enable-preview")
    }
}
