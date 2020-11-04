plugins {
    java
}

java {
    modularity.inferModulePath.set(true)
}


dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")
    implementation("almazko:microjson:0.5")
}
