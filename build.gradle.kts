import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21"
    id("fabric-loom") version "1.13.6"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}



repositories {
    maven {
        setUrl("https://maven.ladysnake.org/releases")
    }
    maven {
        setUrl("https://maven.isxander.dev/releases")
    }
    maven { setUrl("https://maven.maxhenkel.de/releases") }
    maven {
        name = "Modrinth"
        setUrl("https://api.modrinth.com/maven")
    }

    maven { setUrl("https://maven.bawnorton.com/releases") }

    maven { setUrl("https://maven.enjarai.dev/mirrors") }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    modImplementation("maven.modrinth:harpy-express-mod:${project.property("harpy_express_version")}")

    modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-base:${project.property("cca_version")}")
    modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-entity:${project.property("cca_version")}")
    modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-world:${project.property("cca_version")}")
    modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-scoreboard:${project.property("cca_version")}")
    modImplementation("maven.modrinth:midnightlib:${project.property("midnightlib_version")}")

    implementation("de.maxhenkel.voicechat:voicechat-api:${project.property("voicechat_api_version")}")

    modRuntimeOnly("maven.modrinth:simple-voice-chat:fabric-${project.property("voicechat_version")}")
    modRuntimeOnly("de.maxhenkel.voicechat:voicechat-api:${project.property("voicechat_api_version")}:fabric-stub")

    include("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${project.property("mixin_squared_version")}")
    implementation("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${project.property("mixin_squared_version")}")
    annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${project.property("mixin_squared_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
