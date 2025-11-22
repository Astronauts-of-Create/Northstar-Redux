import net.fabricmc.loom.task.RenderDocRunTask
import net.fabricmc.loom.task.RenderDocRunUITask
import java.time.Instant

plugins {
    `maven-publish`
    id("architectury-plugin") version "3.4.161"
    id("dev.architectury.loom") version "1.11.440"
}

version = "0.5.2+1.20.1-create5" // https://semver.org/
group = "com.lightning.northstar" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
    withJavadocJar()
}

architectury {
    platformSetupLoomIde()
    forge()
}

val generatedResources = file("src/generated")

sourceSets.main {
    resources.srcDir(generatedResources)
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/northstar.accessWidener")
    forge {
        mixinConfig("northstar.mixins.json")
    }
    runs["client"].property("mixin.debug.export", "true")
    runs["server"].runDir = "run-server/"
    runs.create("data") {
        data()
        property("forge.logging.markers", "REGISTRIES,REGISTRYDUMP")
        property("forge.logging.console.level", "debug")
        programArgs(
            "--all",
            "--mod", "northstar",
            "--output", generatedResources.absolutePath,
            "--existing", file("src/main/resources").absolutePath
        )
    }
}

project.findProperty("renderdoc")?.let { path ->
    tasks.withType<RenderDocRunTask>().configureEach { renderDocExecutable = file("$path/bin/renderdoccmd") }
    tasks.withType<RenderDocRunUITask>().configureEach { renderDocExecutable = file("$path/bin/qrenderdoc") }
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
    maven("https://squiddev.cc/maven/")
    maven("https://maven.pkg.github.com/copycats-plus/copycats") {
        credentials {
            username = project.property("github.packages.username") as? String
            password = project.property("github.packages.password") as? String
        }
    }
    maven("https://maven.ftb.dev/releases")
    maven("https://cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://api.modrinth.com/maven") {
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://maven.parchmentmc.org/") {
        content {
            includeGroupByRegex("org\\.parchmentmc.*")
        }
    }
    maven("https://maven.latvian.dev/releases") {
        content {
            includeGroupByRegex("dev\\.latvian\\..*")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
    })
    "forge"(libs.forge)

    annotationProcessor(libs.mixinextras.common)
    implementation(libs.mixinextras.common)
    implementation(libs.mixinextras.forge)
    include(libs.mixinextras.forge)

    modImplementation(variantOf(libs.create) { classifier("slim") })
    modImplementation(libs.registrate)
    modImplementation(libs.flywheel.forge)

    modImplementation(libs.geckolib.forge)
    forgeRuntimeLibrary(libs.mclib) // required by GeckoLib

    modImplementation(libs.jei.forge)
    modImplementation(libs.copycats)
    modImplementation(libs.cdg)
    modImplementation(libs.kubejs)
    modImplementation(libs.tfmg)

    // see https://github.com/cc-tweaked/CC-Tweaked/discussions/1752
    forgeRuntimeLibrary("org.squiddev:Cobalt:0.7.3")
    forgeRuntimeLibrary("com.jcraft:jzlib:1.1.3")
    forgeRuntimeLibrary("io.netty:netty-codec-http:4.1.82.Final")
    forgeRuntimeLibrary("io.netty:netty-codec-socks:4.1.82.Final")
    forgeRuntimeLibrary("io.netty:netty-handler-proxy:4.1.82.Final")

    // Embeddium and Oculus have to be installed manually on the client as not to crash the server. keep jCPP as oculus crashes without it.
    forgeRuntimeLibrary(libs.jcpp)

    // Create a folder name "mods-obf" inside "run" and put extra mods needed for testing here
    modLocalRuntime(files(file("run/mods-obf").listFiles() ?: emptyArray<File>()))
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Specification-Title" to "northstar",
            "Specification-Vendor" to "Redstonneur1256",
            "Specification-Version" to version,
            "Implementation-Title" to project.name,
            "Implementation-Version" to version,
            "Implementation-Vendor" to "Redstonneur1256",
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            repositories {
                maven {
                    name = "SkyPlex"
                    credentials(PasswordCredentials::class.java)
                    url = uri("https://repo.mc-skyplex.net/releases/")
                }
            }
        }
    }
}
