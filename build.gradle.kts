import java.util.*


val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val h2_version: String by project
val ktorm_version: String by project
val ktorm_jackson_version: String by project
val postgres_version: String by project
val liquibase_core: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "3.0.0-beta-1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("org.liquibase.gradle") version "2.2.1"
    id("java")
}

group = "textsummarizer"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("com.h2database:h2:$h2_version")
    implementation("io.ktor:ktor-server-metrics-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("com.ucasoft.ktor:ktor-simple-cache:0.+")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache-jvm:0.+")
    implementation("io.ktor:ktor-server-swagger-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")

    // various
    implementation("org.ktorm:ktorm-jackson:$ktorm_jackson_version")

    // client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")

    // database
    implementation("org.ktorm:ktorm-support-postgresql:$ktorm_version")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.apache.commons:commons-dbcp2:2.9.0")

    // liquibase
    liquibaseRuntime("org.liquibase:liquibase-core:$liquibase_core")
    liquibaseRuntime("org.postgresql:postgresql:$postgres_version")
    liquibaseRuntime("info.picocli:picocli:4.7.3")
    liquibaseRuntime("ch.qos.logback:logback-core:1.4.14")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.4.12")
    liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

// database migrations
val dbEnv: String by project.extra

val propertiesFile = file("local.properties")
val properties = Properties()
if (propertiesFile.exists()) {
    properties.load(propertiesFile.inputStream())
    println(properties.getProperty("liquibase.dev.url"))
}

liquibase {
    activities.register("dev") {
        val url = properties.getProperty("liquibase.dev.url") ?: System.getenv("LIQUIBASE_DEV_URL")
        val user = properties.getProperty("liquibase.dev.user") ?: System.getenv("LIQUIBASE_DEV_USER")
        val password = properties.getProperty("liquibase.dev.password") ?: System.getenv("LIQUIBASE_DEV_PASSWORD")

        this.arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "src/main/resources/db/changelog/master.xml",
            "url" to url,
            "username" to user,
            "password" to password,
            "classpath" to "src/main/resources/"
        )
    }

    activities.register("prod") {
        val url = System.getenv("LIQUIBASE_URL")
        val user = System.getenv("LIQUIBASE_USER")
        val password = System.getenv("LIQUIBASE_PASSWORD")

        this.arguments = mapOf(
            "logLevel" to "info",
            "changeLogFile" to "src/main/resources/db/changelog/master.xml",
            "url" to url,
            "username" to user,
            "password" to password,
            "classpath" to "src/main/resources/"
        )
    }

    runList = dbEnv
}