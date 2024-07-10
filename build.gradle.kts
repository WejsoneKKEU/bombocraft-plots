plugins {
    id("java-library")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.1.0"
    kotlin("jvm")
}

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://repo.eternalcode.pl/releases") }
    maven { url = uri("https://jitpack.io") }
}

version = "1.0.0"
group = "com.eternalcode"

java {
}

dependencies {
    // spigot + paper lib
    compileOnly("org.spigotmc:spigot-api:1.19.1-R0.1-SNAPSHOT")
    api("io.papermc:paperlib:1.0.8")


    // anvil gui
    implementation("net.wesjd:anvilgui:1.9.0-SNAPSHOT")

    // adventure
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")

    // LiteCommands
    implementation("dev.rollczi.litecommands:bukkit:2.8.9")
    
    // panda-dzikoysk crafted libs
    implementation("org.panda-lang:expressible:1.3.6")
    implementation("org.panda-lang:panda-utilities:0.5.3-alpha")

    // TriumphGui
    implementation("dev.triumphteam:triumph-gui:3.1.5")

    // okaeri configs
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:4.0.9")

    // ormlite jdbc
    compileOnly("com.j256.ormlite:ormlite-jdbc:6.1")

    // hikari
    compileOnly("com.zaxxer:HikariCP:5.0.1")

    // bStats
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    testImplementation("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("net.kyori:adventure-platform-bukkit:4.3.0")
    testImplementation("net.kyori:adventure-platform-facet:4.3.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.14.0")
    implementation(kotlin("stdlib-jdk8"))
}

bukkit {
    main = "com.eternalcode.plots.PlotsPlugin"
    apiVersion = "1.17"
    website = "https://eternalcode.pl"
    prefix = "EternalPlots"
    author = "EternalCodeTeam"
    name = "EternalPlots"
    description = "Plots plugin for your server!"
    version = "${project.version}"

    libraries = listOf(
        "org.postgresql:postgresql:42.6.0",
        "com.h2database:h2:2.2.222",
        "com.j256.ormlite:ormlite-jdbc:6.1",
        "com.zaxxer:HikariCP:5.0.1",
        "net.kyori:adventure-platform-bukkit:4.3.0",
        "net.kyori:adventure-text-minimessage:4.14.0",
        "org.mariadb.jdbc:mariadb-java-client:3.2.0"
    )

    depend = listOf(
        "Vault"
    )
}

tasks {
    runServer {
        minecraftVersion("1.20.1")
    }

    compileJava {
        options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
        options.encoding = "UTF-8"
    }

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("EternalPlots v${project.version}.jar")

        exclude(
                "META-INF/**",
                "org/intellij/lang/annotations/**",
                "org/jetbrains/annotations/**",
        )

        val prefix = "com.eternalcode.plots.libs"
        listOf(
            "net.dzikoysk",
            "dev.rollczi",
            "org.bstats",
            "org.panda_lang",
            "panda",
            "io.papermc.lib",
            "eu.okaeri",
            "org.checkerframework",
            "org.yaml",
            "com.github",
            "net.wesjd",
            "org.bstats",
            "dev.triumphteam.gui",
            "com.google.gson",
        ).forEach { relocate(it, prefix) }

    }

    getByName<Test>("test") {
        useJUnitPlatform()
    }

}
kotlin {
    jvmToolchain(17)
}
