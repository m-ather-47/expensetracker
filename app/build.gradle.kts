plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.expensetracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.expensetracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // Add packagingOptions to avoid resource merge conflicts (duplicate META-INF files)
    packaging {
        resources {
            // Common files that cause duplicate resource merge errors from different libraries
            excludes += setOf(
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.md",
            )
            // If some files still conflict, pick the first occurrence to avoid build failure
            // pickFirsts can be used for specific known files, example:
            // pickFirsts += setOf("META-INF/whatever.conf")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Room (added)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    //Awesome Validation
    implementation(libs.awesome.validation)

    // JavaMail (for sending email via SMTP)
    implementation(libs.android.mail)
    implementation(libs.android.activation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}