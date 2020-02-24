import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.61" apply false

    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.javamodularity.moduleplugin") version "1.6.0" apply false
    id("kotlinx-serialization") version "1.3.61" apply false
//    id("org.beryx.jlink") version "2.17.2"
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}


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
    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.javamodularity.moduleplugin")


    tasks.compileJava {
        options.compilerArgs = listOf("--enable-preview")
//        extensions.configure<org.javamodularity.moduleplugin.extensions.CompileModuleOptions> {
//            addModules = listOf()
//            compileModuleInfoSeparately = false
//        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.apply {
            jvmTarget = "12"
            noReflect = true
            noStdlib = true
            noJdk = false
            noReflect = true
            includeRuntime = false
            languageVersion = "1.3"
            apiVersion = "1.3"
            suppressWarnings = true
        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains:annotations:18.0.0")
    }

    repositories {
        mavenCentral()
        jcenter()
    }
}
