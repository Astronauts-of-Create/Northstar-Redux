import net.fabricmc.loom.task.RenderDocRunTask
import net.fabricmc.loom.task.RenderDocRunUITask
import java.time.Instant

plugins {
    `maven-publish`
    id("architectury-plugin") version "3.4.161"
    id("dev.architectury.loom") version "1.11.440"
}

version = "0.5.0+1.21.1" // https://semver.org/
group = "com.lightning.northstar" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
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
    neoForge {
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
    maven("https://maven.neoforged.net/releases")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Ponder
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") { // GeckoLib
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroup("com.eliotlash.mclib")
        }
    }
    maven("https://maven.blamejared.com/") // JEI
    maven("https://mvn.devos.one/snapshots")
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
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.1:2024.11.17@zip")
    })
    "neoForge"(libs.neoforge)

    annotationProcessor(libs.mixinextras.common)
    implementation(libs.mixinextras.common)
    implementation(libs.mixinextras.neoforge)

    modImplementation(variantOf(libs.create) { classifier("slim") }) {
        exclude(group = "maven.modrinth", module = "journeymap")
    }
    modImplementation(libs.ponder.neoforge)
    modImplementation(libs.registrate)
    modCompileOnly(libs.flywheel.neoforge.api)
    modRuntimeOnly(libs.flywheel.neoforge)

    modImplementation(libs.geckolib.neoforge)
    forgeRuntimeLibrary(libs.mclib) // required by GeckoLib

    modImplementation(libs.jei.forge)
    modCompileOnly(libs.copycats)
    modImplementation(libs.cdg)
    modImplementation(libs.tfmg)

    // Embeddium and Oculus have to be installed manually on the client as not to crash the server. keep jCPP as oculus crashes without it.
    modRuntimeOnly(libs.jcpp)

    // Create a folder name "mods-obf" inside "run" and put extra mods needed for testing here
    modLocalRuntime(files(file("run/mods-obf-1.21.1").listFiles() ?: emptyArray<File>()))
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
    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(buildProps)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000"))
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
