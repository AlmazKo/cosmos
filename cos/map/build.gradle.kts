plugins {
    java
}

java.modularity.inferModulePath.set(true)

dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")
    implementation(files("../mods/microjson-0.6.3.jar"))
}
