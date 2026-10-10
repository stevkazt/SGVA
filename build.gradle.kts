plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	kotlin("plugin.jpa") version "2.3.21"
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "7.3.1.8318"
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
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	implementation("de.siegmar:fastcsv:3.7.0")
	implementation("org.apache.poi:poi-ooxml:5.4.1")
	implementation("org.apache.pdfbox:pdfbox:3.0.3")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	// SgvaApplicationTests carga el contexto completo de Spring, que desde la
	// implementación del cálculo de autoevaluación requiere esta propiedad
	// (sin valor por defecto en producción, ver application.properties). Se
	// apunta al archivo de ejemplo del modelo 2025 de los fixtures de
	// prueba, no a datos reales de producción.
	environment("SGVA_PONDERACIONES_ARCHIVO", "src/test/resources/fixtures/modelo2025/ponderaciones_modelo_2025.csv")
}

// SonarQube Local Configuration
sonarqube {
    properties {
        property("sonar.projectKey", "SGVA")
        property("sonar.projectName", "SGVA")
        property("sonar.host.url", "http://localhost:9000")
    }
}
