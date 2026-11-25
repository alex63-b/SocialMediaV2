import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.5"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("Main")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.14")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}