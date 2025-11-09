import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Base64

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
    `maven-publish`
    signing
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
            version = "0.1.2"

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

/////////////////////// kover coverage ///////////////////////
kover {
    reports {
        filters {
            excludes {
                // Exclude Android generated files
                classes("*.BuildConfig", "*.R", "*.R$*")
            }
        }

        verify {
            rule {
                minBound(90)
            }
        }
    }
}
