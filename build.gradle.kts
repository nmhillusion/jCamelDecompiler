plugins {
    id("java")
    id("application")
    id("distribution")
}

group = "tech.nmhillusion.jCamelDecompilerApp"
version = "2026.1.0"

var appNameL = "jCamelDecompilerApp"
var mainClassL = "tech.nmhillusion.jCamelDecompilerApp.Main"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // https://jitpack.io/#nmhillusion/n2mix-java
    implementation("com.github.nmhillusion:n2mix-java:2025.5.12")
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

val generateVersionProperties by tasks.registering {
    val propertiesFile = file("src/main/resources/config/app-info.yml")
    outputs.file(propertiesFile)

    doLast {
        propertiesFile.parentFile.mkdirs()
        propertiesFile.writeText("info:\n  name: ${appNameL}\n  version: ${project.version}\n  author: nmhillusion\n")
    }
}

tasks.named("processResources") {
    dependsOn(generateVersionProperties)
}

application {
    mainClass = mainClassL
    applicationName = appNameL
}

distributions {
    main {
        distributionBaseName = appNameL
        contents {
            from("src/main/resources") {
                exclude("scripts/startup.bat")
            }
            into("bin") {
                from("src/main/resources/scripts/startup.bat")
            }
        }
    }
}
