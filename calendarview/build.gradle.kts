import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish") version "0.34.0"
}
version = project.findProperty("libraryVersion") ?: "1.0.0-alpha7"

android {
    namespace = "com.zapplications.calendarview"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        target {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(project(":core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    mavenPublishing {
        coordinates(
            groupId = "io.github.iamsuatzengin",
            artifactId = "calendarview",
            version = project.version.toString()
        )

        pom {
            name.set("ZCalendar-Andro")
            description.set("ZCalendar is a simple calendar view for Android.")
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
