import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Base64

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
    signing
    jacoco
}

/** Get a Gradle property if available, or use an environment variable instead.*/
fun getPropertyOrEnv(name: String): String? = (findProperty(name) as String?) ?: System.getenv(name)

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        mavenPublication {
            // Set the artifact ID for Android publication
            artifactId = "bark-android"
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                    // Backward compatibility settings
                    apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
                    languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)

                    freeCompilerArgs.add("-Xsuppress-version-warnings") // Suppress version warnings
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        // Set iOS framework details
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
        // Set Kotlin version target
        target.compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
                    languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.ivangarzab.bark"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

/////////////////////// publishing ///////////////////////
val gpgPassphrase = getPropertyOrEnv("GPG_PASSPHRASE")
val gpgSecretKey = getPropertyOrEnv("GPG_PRIVATE_KEY")
val mavenUsername = getPropertyOrEnv("MAVEN_USERNAME")
val mavenPassword = getPropertyOrEnv("MAVEN_PASSWORD")

publishing {
    publications {
        publications.withType<MavenPublication> {
            artifactId = artifactId.replace("shared", "bark")
            groupId = "com.ivangarzab"
            version = "0.1.1"

            pom {
                name = "barK"
                description = "A simple, lightweight logging library for Kotlin Multiplatform projects."
                url = "https://github.com/ivangarzab/bark"
                inceptionYear = "2025"
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://spdx.org/licenses/Apache-2.0.html"
                    }
                }
                developers {
                    developer {
                        id = "ivangarzab"
                        name = "Iván Garza Bermea"
                        email = "ivangb6@gmail.com"
                    }
                }
                scm {
                    url = "https://github.com/ivangarzab/barK"
                    connection = "scm:git:git://github.com/ivangarzab/barK.git"
                    developerConnection = "scm:git:ssh://github.com/ivangarzab/barK.git"
                }
            }
        }
    }

    repositories {
        maven {
            name = "ossrh-staging-api"
            url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            credentials {
                username = mavenUsername
                password = mavenPassword
            }
        }
    }
}

signing {
    if (gpgSecretKey != null && gpgPassphrase != null) {
        // Decode base64-encoded key
        val decodedKey = String(Base64.getDecoder().decode(gpgSecretKey))
        useInMemoryPgpKeys(decodedKey, gpgPassphrase)
        sign(publishing.publications)
    }
}

 /////////////////////// jacoco testing ///////////////////////
jacoco {
    toolVersion = "0.8.11"
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    group = "reporting"
    description = "Generate Jacoco coverage reports for barK"

    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/detectors/**", // Optional: exclude platform detection utilities if you want
    )

    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = listOf(
        "${project.projectDir}/src/commonMain/kotlin",
        "${project.projectDir}/src/androidMain/kotlin"
    )

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec")
    })

    finalizedBy("jacocoTestCoverageVerification")
}

tasks.register("jacocoTestCoverageVerification", JacocoCoverageVerification::class) {
    dependsOn("jacocoTestReport")

    violationRules {
        rule {
            enabled = true

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.0".toBigDecimal() // Start with 0% - no enforcement
            }
        }
    }
}
