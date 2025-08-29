import java.time.Instant

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.9.+"
}

version = "0.2.8-SNAPSHOT+1.20.1" // https://semver.org/
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
    accessWidenerPath = rootProject.file("src/main/resources/northstar.accessWidener")
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
    maven("https://maven.pkg.github.com/copycats-plus/copycats") {
        credentials {
            username = project.property("github.packages.username") as? String
            password = project.property("github.packages.password") as? String
        }
    }
    maven("https://cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://api.modrinth.com/maven/")
    maven("https://maven.parchmentmc.org")
}

dependencies {
    minecraft(libs.minecraft)

    //Using mincraft mojang AND parchment mappings
    //https://parchmentmc.org/docs/getting-started
    //https://docs.architectury.dev/plugin/get_started
    //https://ldtteam.jfrog.io/artifactory/parchmentmc-public/org/parchmentmc/data/parchment-1.20.1/2023.09.03/
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
    })

    "forge"(libs.forge)
    annotationProcessor(libs.mixinextras.common)
    implementation(libs.mixinextras.forge)

    modRuntimeOnly(libs.embeddium)//Embeddium (required by oculus)

    //Oculus
    //https://github.com/Asek3/Oculus/blob/1.16.5/build.gradle
    //java.lang.NoClassDefFoundError: org/anarres/cpp/PreprocessorListener
    // https://mvnrepository.com/artifact/org.anarres/jcpp
    runtimeOnly("org.anarres:jcpp:1.4.14")
    modRuntimeOnly(libs.oculus)

    //Create
    modImplementation(variantOf(libs.create) { classifier("slim") })
    modImplementation(libs.ponder.forge)
    modImplementation(libs.registrate)
    modCompileOnly(libs.flywheel.forge.api)
    modRuntimeOnly(libs.flywheel.forge)

    // Geckolib
    modImplementation(libs.geckolib.forge)
    forgeRuntimeLibrary(libs.mclib) // required by GeckoLib

    modImplementation(libs.jei.forge)
    modImplementation(libs.copycats)

    modLocalRuntime(files(file("run/mods-obf").listFiles() ?: emptyArray<File>()))
}

tasks.processResources {
    outputs.upToDateWhen { false }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "northstar",
                "Specification-Vendor" to "Redstonneur1256",
                "Specification-Version" to version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to "Redstonneur1256",
                "Implementation-Timestamp" to Instant.now().toString()
            )
        )
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
