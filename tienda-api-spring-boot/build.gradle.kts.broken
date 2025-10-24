plugins {  
    jacoco  
}  

dependencies {  
    testImplementation("org.testcontainers:junit-jupiter")  
    testImplementation("org.testcontainers:postgresql")  
    testImplementation("org.testcontainers:mongodb")  
    testImplementation("org.testcontainers:testcontainers")  
}  

jacoco {  
    toolVersion = "0.8.7" // Use the latest version  
    reports {  
        xml.isEnabled = true  
        csv.isEnabled = true  
        html.isEnabled = true  
    }  
    exclusions = listOf("com.yourpackage.config.*", "com.yourpackage.model.*") // Adjust package according to your structure  
    verification {  
        rule {  
            limit {  
                minimum = 0.75 // 75% for lines  
            }  
        }  
        rule {  
            limit {  
                minimum = 0.70 // 70% for branches  
            }  
        }  
    }  
}  
