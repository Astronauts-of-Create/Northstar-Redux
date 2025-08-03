import java.time.Instant

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.9.+"
}

version = "0.2.3-SNAPSHOT+1.20.1" // https://semver.org/
group = "com.lightning.northstar" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    forge {
        mixinConfig("northstar.mixins.json")
    }
}

repositories {
    mavenCentral()
    maven("https://modmaven.dev/")
    maven("https://maven.tterrag.com/")
    maven("https://maven.createmod.net")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Ponder
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") { // GeckoLib
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroup("com.eliotlash.mclib")
        }
    }
    maven("https://maven.blamejared.com/") // JEI
}

dependencies {
    // TODO: regroup versions in a separate place
    minecraft("com.mojang:minecraft:1.20.1")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:1.20.1-47.4.0")

    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")
    implementation("io.github.llamalad7:mixinextras-forge:0.4.1")

    modImplementation("com.simibubi.create:create-1.20.1:6.0.6-205:slim")
    modImplementation("net.createmod.ponder:Ponder-Forge-1.20.1:1.0.83")
    modImplementation("com.tterrag.registrate:Registrate:MC1.20-1.3.3")
    modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-1.20.1:1.0.2")
    modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-1.20.1:1.0.2")

    modImplementation("software.bernie.geckolib:geckolib-forge-1.20.1:4.7.2")
    forgeRuntimeLibrary("com.eliotlash.mclib:mclib:20") // required by GeckoLib

    modImplementation("mezz.jei:jei-1.20.1-forge:15.20.0.112")

    modLocalRuntime(files(file("run/mods-obf").listFiles()))
}

tasks.processResources {
    outputs.upToDateWhen { false }
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Specification-Title"      to "northstar",
            "Specification-Vendor"     to "Redstonneur1256",
            "Specification-Version"    to version,
            "Implementation-Title"     to project.name,
            "Implementation-Version"   to version,
            "Implementation-Vendor"    to "Redstonneur1256",
            "Implementation-Timestamp" to Instant.now().toString()
        ))
    }
}

tasks.processResources {
    val buildProps = project.properties.toMutableMap()
    buildProps["file"] = mapOf("jarVersion" to project.version)
    filesMatching(listOf("META-INF/mods.toml")) {
        expand(buildProps)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
