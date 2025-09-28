plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("com.vanniktech.maven.publish") version "0.34.0"
}

version = project.findProperty("libraryVersion") ?: "1.0.0-alpha7"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    testImplementation(libs.junit)
}

afterEvaluate {
    mavenPublishing {
        coordinates(
            groupId = "io.github.iamsuatzengin",
            artifactId = "core",
            version = project.version.toString()
        )

        pom {
            name.set("ZCalendar-Core")
            description.set("ZCalendar core is a simple core logic.")
            url.set("https://github.com/iamsuatzengin/zcalendar")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    name.set("Suat Zengin")
                    email.set("zenginsuat.inf@gmail.com")
                    url.set("https://github.com/iamsuatzengin")
                }
            }

            scm {
                connection = "scm:git@github.com:iamsuatzengin/zcalendar.git"
                developerConnection = "scm:git@github.com:iamsuatzengin/zcalendar.git"
                url = "https://github.com/iamsuatzengin/zcalendar.git"
            }
        }
    }
}
