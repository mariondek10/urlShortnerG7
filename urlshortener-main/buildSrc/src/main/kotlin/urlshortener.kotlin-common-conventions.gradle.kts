import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
}

kover {
    excludeJavaCode()
}

koverReport {
    defaults {
        log {
            groupBy = GroupingEntityType.CLASS
        }
    }
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<Detekt>("detekt") {
    exclude("**/generated-sources/**")
}

tasks.named("check") {
    dependsOn("detekt")
}
