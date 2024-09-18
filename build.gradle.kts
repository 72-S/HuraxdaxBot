plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.consti"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

application {
    mainClass.set("dev.consti.Main")
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}

tasks.named("startShadowScripts") {
    dependsOn("shadowJar", "jar")
}

tasks.named("distZip") {
    dependsOn("shadowJar")
}

tasks.named("distTar") {
    dependsOn("shadowJar")
}

tasks.named("startScripts") {
    dependsOn("shadowJar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

