plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "tech.nmhillusion.jCamelDecompilerApp"
version = "1.0"

var appNameL = "jCamelDecompilerApp"
var mainClassL = "tech.nmhillusion.jCamelDecompilerApp.Main"

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

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from("src/main/resources").exclude(
        "decompiler",
        "icon",
        "scripts"
    )

    manifest {
        attributes["Main-Class"] = mainClassL // Optional: if you need an executable jar
    }
    // You might configure the base archive name, version, etc. here if needed
    // archiveBaseName.set("my-app")
    // archiveVersion.set("1.0.0")
}

tasks.distZip {
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass = mainClassL
    applicationName = appNameL
}

distributions {
    main {
        distributionBaseName = appNameL
        contents {
            from("src/main/resources")
        }
    }
}