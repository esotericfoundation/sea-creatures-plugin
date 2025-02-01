import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    java
    `java-library`

    `maven-publish`

    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

description = "A Minecraft java plugin that enhances your fishing experience! Adds multiple custom sea creatures, all with special custom abilities and AI."

group = "org.rolypolyvole"
version = "1.0-SNAPSHOT"

val projectGroupString = group.toString()
val projectVersionString = version.toString()

val javaVersion = 21
val javaVersionEnumMember = JavaVersion.valueOf("VERSION_$javaVersion")

val paperApiMinecraftVersion = "1.21.1"
val paperApiVersion = "$paperApiMinecraftVersion-R0.1-SNAPSHOT"

java {
    sourceCompatibility = javaVersionEnumMember
    targetCompatibility = javaVersionEnumMember

    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(paperApiVersion)
}

tasks {
    compileJava {
        options.release.set(javaVersion)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
    name = "SeaCreatures"
    description = project.description

    authors = listOfNotNull("rolyPolyVole")

    setVersion(project.version)

    apiVersion = paperApiMinecraftVersion
    main = "${project.group}.seacreatures.${name.get()}Plugin"

    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
}
