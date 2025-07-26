import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.changelog.Changelog.OutputType.MARKDOWN

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.6.0"
    id("org.jetbrains.changelog") version "2.0.0"
}

group = "com.vel"
version = "1.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration {
        group = "com.vel"
        changeNotes.set(provider { recentChanges(HTML) })
        ideaVersion.sinceBuild.set(project.property("sinceBuild").toString())
        ideaVersion.untilBuild.set(provider { null })
    }
    buildSearchableOptions.set(false)
    instrumentCode = true
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
//intellij {
//    version.set("2024.1.1")
//    type.set("IC") // Target IDE Platform
//    plugins.set(listOf("android","gradle"))
//}

dependencies {
    intellijPlatform {
        androidStudio(property("ideVersion").toString())
        bundledPlugin("org.jetbrains.android")
        instrumentationTools()
    }
}

changelog {
    repositoryUrl.set("https://github.com/velgun/AndroidBuildVariantPlugin")
    itemPrefix.set("-")
    groups.empty()
    combinePreReleases.set(true)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks.register("printLastChanges") {
    doLast {
        println(recentChanges(outputType = MARKDOWN))
        println(recentChanges(outputType = HTML))
    }
}

fun recentChanges(outputType: Changelog.OutputType): String {
    var s = ""
    changelog.getAll().toList().drop(1) // drop the [Unreleased] section
        .take(5) // last 5 changes
        .forEach { (key, _) ->
            s += changelog.renderItem(
                changelog.get(key).withHeader(true).withEmptySections(false), outputType
            )
        }

    return s
}