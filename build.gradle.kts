plugins {
    id("java")
    application
}

group = "dev.consti"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:5.0.1")
}

application {
    mainClass.set("dev.consti.Main")
}

tasks.test {
    useJUnitPlatform()
}