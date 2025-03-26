plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "tech.nmhillusion.jCamelDecoderApp"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.nmhillusion:n2mix-java:2024.9.3")
    // https://mvnrepository.com/artifact/org.yaml/snakeyaml
    implementation("org.yaml:snakeyaml:2.4")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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