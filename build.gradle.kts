import java.util.*

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val ktormVersion: String by project
val postgresVersion: String by project
val liquibaseCoreVersion: String by project
val mockkVersion: String by project

plugins {
    kotlin("jvm") version "2.0.10"
    id("io.ktor.plugin") version "3.0.0-beta-1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("org.liquibase.gradle") version "2.2.1"
}

group = "textsummarizer"
version = "0.0.2"

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
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-swagger-jvm")
    implementation("io.ktor:ktor-server-openapi-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-config-yaml-jvm")

    // logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // caching
    implementation("com.ucasoft.ktor:ktor-simple-cache-jvm:0.+")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache-jvm:0.+")

    // monitoring
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm")
    implementation("io.micrometer:micrometer-registry-prometheus:1.13.+")

    // client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")

    // database
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-jackson:$ktormVersion")
    implementation("org.ktorm:ktorm-support-postgresql:$ktormVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.apache.commons:commons-dbcp2:2.9.0")
    implementation("com.h2database:h2:$h2Version")

    // liquibase
    liquibaseRuntime("org.liquibase:liquibase-core:$liquibaseCoreVersion")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.7.3")
    liquibaseRuntime("ch.qos.logback:logback-core:1.4.14")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.4.12")
    liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")

    // testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.0.0-beta-1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
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

ktor {
    docker {
        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "textsummarizer-backend" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
        imageTag.set(version.toString())
    }
}