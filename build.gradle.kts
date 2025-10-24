// TestContainers dependencies for PostgreSQL, MongoDB, Redis
implementation("org.testcontainers:postgresql:1.17.3")
implementation("org.testcontainers:mongodb:1.17.3")
implementation("org.testcontainers:redis:1.17.3")

// Jacoco plugin configuration for code coverage
jacoco {
    toolVersion = "0.8.7"
    reportsDir = file("${buildDir}/jacoco-reports")
}

tasks.jacocoTestReport {
    dependsOn test // Tests are required to run before generating the report
    reports {
        xml.enabled true
        csv.enabled false
        html.enabled true
    }
}
