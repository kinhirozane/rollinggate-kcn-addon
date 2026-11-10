import com.diffplug.spotless.LineEnding
import me.modmuss50.mpp.ReleaseType
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment

plugins {
    `java-library`
    alias(libs.plugins.moddev)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spotless.changelog)
    alias(libs.plugins.mod.publish.plugin)
    `maven-publish`
    idea
}

val modGroup = providers.gradleProperty("mod.group").get()
val modVersion = providers.gradleProperty("mod.version").get()
val modNamespace = providers.gradleProperty("mod.namespace").get()
val modDescription = providers.gradleProperty("mod.description").get()
val minecraftVersion = providers.gradleProperty("minecraft.version").get()
val javaVersion = providers.gradleProperty("java.version").get()

group = modGroup
version = "$modVersion+$minecraftVersion"
description = modDescription

base.archivesName = modNamespace
// Mojang ships Java 25 to end users in 26.1.2, so mods should target Java 25.
java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)

neoForge {
    // Specify the version of NeoForge to use.
    version = providers.gradleProperty("neoforge.version").get()

    // This line is optional. Access Transformers are automatically detected
    accessTransformers.from(project.files("src/main/resources/META-INF/accesstransformer.cfg"))

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        // applies to all the run configs above
        configureEach {
            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            logLevel = org.slf4j.event.Level.DEBUG

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", modNamespace)
        }

        create("client") {
            client()
        }

        create("server") {
            server()
            programArgument("--nogui")
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        create("gameTestServer") {
            type = "gameTestServer"
        }

        create("data") {
            clientData()
            // example of overriding the workingDirectory set in configureEach above, uncomment if you want to use it
            // gameDirectory = project.file('run-data')

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll(
                listOf(
                    "--mod", modNamespace,
                    "--all",
                    "--output", file("src/generated/resources/").absolutePath,
                    "--existing", file("src/main/resources/").absolutePath
                )
            )
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // multi mod projects should define one per mod
        create(modNamespace) {
            sourceSet(sourceSets["main"])
        }
    }

    // Run the JUnit test task inside the modded environment, with this mod loaded.
    unitTest {
        enable()
        testedMod = mods.getByName(modNamespace)
    }
}

// Sets up a dependency configuration called 'localRuntime'.
// This configuration should be used instead of 'runtimeOnly' to declare
// a dependency that will be present for runtime testing but that is
// "optional", meaning it will not be pulled by dependents of this mod.
configurations {
    //runtimeClasspath.get().extendsFrom(localRuntime.get())
}

repositories {
    // Add here additional repositories if required by some of the dependencies below.
    mavenCentral()
    maven("https://server.cjsah.net:1002/maven/")
}

dependencies {
    implementation(libs.rollinggate)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

// This block of code expands all declared replace properties in the specified resource targets.
// A missing property will result in an error. Properties are expanded using ${} Groovy notation.
val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    description = ""
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to "[$minecraftVersion]",
        "neoforge_version" to providers.gradleProperty("neoforge.version").get(),
        "mod_namespace" to modNamespace,
        "mod_name" to providers.gradleProperty("mod.name").get(),
        "mod_license" to providers.gradleProperty("mod.license").get(),
        "mod_version" to modVersion,
        "mod_description" to providers.gradleProperty("mod.description").get(),
        "mod_authors" to providers.gradleProperty("mod.authors").get(),
        "rolling_gate_version" to libs.versions.rollinggate.get()
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

// Include the output of "generateModMetadata" as an input directory for the build
// this works with both building through Gradle and the IDE.
sourceSets["main"].resources.srcDir(generateModMetadata)
// To avoid having to run "generateModMetadata" manually, make it run on every project reload
neoForge.ideSyncTask(generateModMetadata)

spotless {
    java {
        target("src/main/java/**/*.java")
        licenseHeaderFile(file("spotless/license-header.txt"))
        endWithNewline()
        removeUnusedImports()
        lineEndings = LineEnding.UNIX
    }
}

// Example configuration to allow publishing using the maven-publish plugin
publishing {
    repositories {
        mavenLocal()
    }

    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

spotlessChangelog {
    changelogFile(file("CHANGELOG.md"))
    enforceCheck(true)
}

publishMods {
    file = tasks.jar.get().archiveFile
    changelog = spotlessChangelog.parsedChangelog.unreleasedChanges().trim()
    version = project.version.toString()
    type = ReleaseType.STABLE
    displayName = providers.gradleProperty("mod.name")
    modLoaders.add("neoforge")

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = "1786670"
        minecraftVersions.add(minecraftVersion)
        javaVersions.add(JavaVersion.VERSION_25)
        client = true
        server = true
        requires("rolling-gate")
    }

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "SDPpJg3s"
        minecraftVersions.add(minecraftVersion)
        environment = ModrinthEnvironment.CLIENT_AND_SERVER
        requires {
            id = "HXsmHiKu"
            version = "DzHYexPP"
        }
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = "kinhirozane/rollinggate-kcn-addon"
        commitish = "master"
        tagName = "v${project.version}"
        displayName = "${providers.gradleProperty("mod.name").get()} v${project.version}"
    }
}

tasks.clean {
    doFirst {
        val dir = layout.buildDirectory.get().asFile
        setDelete(
            if (dir.resolve("moddev").exists()) dir.listFiles()?.filter { f -> f.name != "moddev" } ?: emptyList()
            else listOf(dir)
        )
    }
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
