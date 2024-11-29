plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "tech.nmhillusion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "tech.nmhillusion.Main"
}

distributions {
    main {
        distributionBaseName = "jCamelApp"
        contents {
            from("src/main/resources")
        }
    }
}