plugins {
    id("java")
    id("war")
    id("com.github.bjornvester.xjc") version "1.8.2"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.ws:spring-ws-core:4.0.11")
    implementation("org.springframework:spring-webflux:6.1.13")
    implementation("io.projectreactor.netty:reactor-netty:1.1.22")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("org.springframework:spring-webmvc:6.1.13")
    implementation("wsdl4j:wsdl4j:1.6.3")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation(platform("org.springframework:spring-framework-bom:6.1.13"))
    implementation("org.springframework:spring-context")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-jcl:2.23.1")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
}





tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName.set("hr-service.war")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/xjc/java"))
        }
    }
}

xjc {
    xsdDir.set(file("src/main/resources/xsd"))
    outputJavaDir.set(layout.buildDirectory.dir("generated/sources/xjc/java").get().asFile)
    defaultPackage.set("com.example.generated")
}
