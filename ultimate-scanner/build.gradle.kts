plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "in.innovateria.ultimate_scanner"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    publications {
        create<MavenPublication>("ultimate-scanner") {
            groupId = "in.innovateria"
            artifactId = "ultimate_scanner"
            version = "1.0.0" // Specify your version here

            // Optionally, you can include additional information
            pom {
                name.set("Ultimate Scanner")
                description.set("A QR code scanner library")
                url.set("https://github.com/Vnjvibhash/UltimateScanner")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("vnjvibhash")
                        name.set("Vivek Kumar")
                        email.set("vnjvibhash@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Vnjvibhash/UltimateScanner.git")
                    developerConnection.set("scm:git:ssh://github.com/Vnjvibhash/UltimateScanner.git")
                    url.set("https://github.com/Vnjvibhash/UltimateScanner")
                }
            }
        }
    }
    repositories {
        mavenLocal() // Publish to local Maven repository for testing
        // Add other repositories as needed, e.g., Maven Central or custom repository
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.core)
}