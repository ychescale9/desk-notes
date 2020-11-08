import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.jetbrains.compose") version "0.1.0-m1-build62"
    application
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation(compose.desktop.all)
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}

application {
    mainClass.set("io.github.reactivecircus.desknotes.DeskNotes")
}
