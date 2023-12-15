plugins {
    id("urlshortener.spring-library-conventions")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("commons-validator:commons-validator:${Version.COMMONS_VALIDATOR}")
    implementation("com.google.guava:guava:${Version.GUAVA}")
    implementation("eu.bitwalker:UserAgentUtils:1.21")
    //implementation(project(mapOf("path" to ":buildSrc")))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")



    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${Version.MOCKITO}")
}
