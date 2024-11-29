plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "tech.nmhillusion.jCamelDecoderApp"
version = "1.0"

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
    mainClass = "tech.nmhillusion.jCamelDecoderApp.Main"
}

distributions {
    main {
        distributionBaseName = "jCamelDecoderApp"
        contents {
            from("src/main/resources")
        }
    }
}