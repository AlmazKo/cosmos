plugins {
    java
}

java {
    modularity.inferModulePath.set(true)
}


dependencies {
    compileOnly("org.jetbrains:annotations:19.0.0")
    implementation("almazko:microjson:0.5")
}
