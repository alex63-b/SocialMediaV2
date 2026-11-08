plugins {
    id("java")
}

group = "curs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // curs 6: driverul JDBC pentru PostgreSQL
    implementation("org.postgresql:postgresql:42.7.7")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
