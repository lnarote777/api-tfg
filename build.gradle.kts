plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	war
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Add coroutines core
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3") // For reactive support

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	//MongoDB
	implementation("org.mongodb:mongodb-driver-kotlin-sync:5.3.0")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	//Stripe (Pago)
	implementation("com.stripe:stripe-java:24.0.0")

	implementation("com.google.code.gson:gson:2.10.1")

	// === DEPENDENCIAS DE TEST ===
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// MockK para mocking en Kotlin
	testImplementation("io.mockk:mockk:1.13.8") // Updated to latest stable version
	testImplementation("io.mockk:mockk-agent-jvm:1.13.8") // Required for coroutine support
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation("com.ninja-squad:springmockk:4.0.2")

	// Coroutines test support
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") {
		exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
	}

	// Para testing con MongoDB embebido
	testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.11.0")

	// Para testing de containers (alternativa más robusta)
	testImplementation("org.testcontainers:junit-jupiter:1.19.3")
	testImplementation("org.testcontainers:mongodb:1.19.3")

	// Para assertions más expresivas
	testImplementation("org.assertj:assertj-core:3.24.2")

	// Para testing de JSON
	testImplementation("com.jayway.jsonpath:json-path:2.8.0")

	// Para testing web con MockMvc
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	// Hamcrest para matchers adicionales si los necesitas
	testImplementation("org.hamcrest:hamcrest-all:1.3")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
