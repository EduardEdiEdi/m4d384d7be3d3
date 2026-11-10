// One plugin jar from three source sets:
//   light  - the LightAntiCheat code base (interaction module), compiled against the old Spigot API it was written for
//   better - the BetterAnticheat code base (combat module), compiled against Paper + packetevents
//   main   - the host plugin (host/), which joins both and fires kestrel.api.DetectionEvent
plugins {
    java
    id("com.gradleup.shadow") version "9.2.2"
}

group = "kestrel"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.tcoded.com/releases")
    maven("https://repo.opencollab.dev/main/")
    maven("https://german-code-repo.haldor.xyz/mirror")
    maven("https://jitpack.io")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

val lombok = "org.projectlombok:lombok:1.18.48"
val folia = "com.tcoded:FoliaLib:0.5.2"

sourceSets {
    create("light") {
        java.srcDir("light/src/main/java")
        resources.srcDir("light/src/main/resources")
    }
    create("better") {
        java.srcDirs("better/core/src/main/java", "better/paper/src/main/java")
        resources.srcDir("better/core/src/main/resources")
    }
    main {
        java.setSrcDirs(listOf("host/src/main/java"))
        resources.setSrcDirs(listOf("host/src/main/resources"))
    }
}

val lightImplementation by configurations.getting
val lightCompileOnly by configurations.getting
val betterImplementation by configurations.getting
val betterCompileOnly by configurations.getting

dependencies {
    // light
    lightCompileOnly(files("light/comp/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar"))
    lightCompileOnly("io.netty:netty-all:4.2.7.Final")
    lightCompileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    lightCompileOnly(lombok)
    "lightAnnotationProcessor"(lombok)
    lightImplementation("org.jetbrains:annotations:26.0.2")
    lightImplementation(folia)
    lightImplementation("com.tchristofferson:ConfigUpdater:2.2")
    lightImplementation(fileTree("light/impl/multiversion"))

    // better
    betterCompileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    betterCompileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    betterCompileOnly("org.apache.fory:fory-core:0.11.2")
    betterCompileOnly("it.unimi.dsi:fastutil:8.5.16")
    betterCompileOnly("org.yaml:snakeyaml:2.4")
    betterCompileOnly("org.slf4j:slf4j-api:2.0.17")
    betterCompileOnly("com.google.code.findbugs:jsr305:3.0.2")
    betterCompileOnly(lombok)
    "betterAnnotationProcessor"(lombok)
    betterImplementation("wtf.spare:sparej:1.0.1") { exclude(group = "it.unimi.dsi") } // fastutil comes with Paper
    betterImplementation(folia)

    // host
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly(sourceSets["light"].output)
    compileOnly(sourceSets["better"].output)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.add("-Xlint:-options")
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("paper-plugin.yml") { expand(props) }
}

tasks.shadowJar {
    archiveBaseName.set("Kestrel")
    archiveClassifier.set("")
    from(sourceSets["light"].output, sourceSets["better"].output)
    configurations = listOf(project.configurations["lightRuntimeClasspath"], project.configurations["betterRuntimeClasspath"])
    from("LICENSE")
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA", "META-INF/versions/**", "plugin.yml")
    relocate("com.tcoded.folialib", "kestrel.lib.folialib")
    relocate("com.tchristofferson", "kestrel.lib.configupdater")
    relocate("com.fren_gor", "kestrel.lib.fren_gor")
    relocate("wtf.spare.sparej", "kestrel.lib.sparej")
    mergeServiceFiles()
}

tasks.build { dependsOn(tasks.shadowJar) }
