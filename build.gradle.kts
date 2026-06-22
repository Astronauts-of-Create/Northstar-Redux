import java.time.Instant

plugins {
    `maven-publish`
    id("net.neoforged.moddev.legacyforge") version "2.0.141"
    id("net.kyori.blossom") version "2.2.0"
    kotlin("jvm") version "2.1.21"
}

version = "0.6.0+1.20.1" // https://semver.org/
group = "com.lightning.northstar" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
    withJavadocJar()
}

val generatedResources = file("src/generated")

sourceSets.main {
    resources.srcDir(generatedResources)

    blossom.javaSources {
        property("version", version.toString())
    }
}

legacyForge {
    version = "1.20.1-47.4.0"

    parchment {
        minecraftVersion = "1.20.1"
        mappingsVersion = "2023.09.03"
    }

    validateAccessTransformers = true
    interfaceInjectionData.from("interfaces.json")

    runs {
        configureEach {
            systemProperty("geckolib.disable_examples", "true")
            systemProperty("mixin.debug.export", "true")
            //systemProperty("forge.logging.markers", "REGISTRIES,REGISTRYDUMP")
            //systemProperty("forge.logging.console.level", "debug")
        }

        create("client") {
            client()
            gameDirectory = file("run")
            jvmArgument("-Xmx6G")
        }
        create("data") {
            data()
            gameDirectory = file("run")
            programArguments.addAll(
                "--all",
                "--mod", "northstar",
                "--output", generatedResources.absolutePath,
                "--existing", file("src/main/resources").absolutePath
            )
        }
        create("server") {
            server()
            gameDirectory = file("run-server")
        }
    }

    mods {
        create("northstar") {
            sourceSet(sourceSets.main.get())
        }
    }
}

mixin {
    add(sourceSets.main.get(), "northstar.refmap.json")
    config("northstar.mixins.json")
}

repositories {
    mavenCentral()
    maven("https://modmaven.dev/")
    maven("https://maven.tterrag.com/")
    maven("https://maven.createmod.net")
    maven("https://maven.architectury.dev/")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Ponder
    maven("https://maven.blamejared.com/") // JEI
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") { // GeckoLib
        content {
            includeGroupByRegex("software\\.bernie.*")
            includeGroup("com.eliotlash.mclib")
        }
    }
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
    maven("https://maven.valkyrienskies.org") {
        content {
            includeGroupByRegex("org\\.valkyrienskies.*")
        }
    }
    flatDir { dir("run/mods-obf") }
}

dependencies {
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })
    compileOnly(libs.mixin)
    annotationProcessor(libs.mixinextras.common)
    implementation(libs.mixinextras.common)
    implementation(libs.mixinextras.forge)
    jarJar(libs.mixinextras.forge)

    modImplementation(variantOf(libs.create) { classifier("slim") })
    modImplementation(libs.ponder.forge)
    modImplementation(libs.registrate)
    modCompileOnly(libs.flywheel.forge.api)
    modRuntimeOnly(libs.flywheel.forge)

    modImplementation(libs.geckolib.forge)

    modCompileOnly(libs.oculus)

    modImplementation(libs.architectury)
    modImplementation(libs.jei.forge)
    modImplementation(libs.copycats)
    modImplementation(libs.cdg)
    modImplementation(libs.kubejs)
    modImplementation(libs.rhino)
    modImplementation(libs.tfmg)

    modImplementation(libs.valkyrienskies.mod.forge)
    implementation(libs.valkyrienskies.api) { isTransitive = false }
    implementation(libs.valkyrienskies.util) { isTransitive = false }
    implementation(libs.valkyrienskies.internal) { isTransitive = false }
    implementation(libs.kotlinforforge)
    compileOnly("org.joml:joml:1.10.4")
    compileOnly("org.joml:joml-primitives:1.10.0")

    // Create a folder name "mods-obf" inside "run" and put extra mods needed for testing here
    file("run/mods-obf").listFiles()?.forEach { modRuntimeOnly("local:${it.nameWithoutExtension}") }
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
            "Implementation-Timestamp" to Instant.now().toString(),
            "MixinConfigs" to "northstar.mixins.json"
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
