plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    val locales = listOf(
        "en", "cs", "de", "el", "es", "fr", "hi", "it", "ja", "ka",
        "nl", "pl", "pt", "ru", "sr", "b+sr+Latn", "tr", "vi",
        "zh-rCN", "zh-rTW",
    )
    namespace = "de.rwth_aachen.phyphox"
    testNamespace = "de.rwth_aachen.phyphoxTest"
    compileSdk {
        version = release(libs.versions.compileSdk.get().toInt()) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "de.rwth_aachen.phyphox"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        versionName = "1.2.0"
        //  format  WXXYYZZ, where WW is major, XX is minor, YY is patch, and ZZ is build
        versionCode = 1020009 //1.02.00-09

        buildConfigField(
            "String[]",
            "LOCALE_ARRAY",
            "new String[]{\"" + locales.joinToString("\",\"") + "\"}",
        )

        vectorDrawables {
            useSupportLibrary = true
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    androidResources {
        @Suppress("UnstableApiUsage")
        localeFilters += locales
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    flavorDimensions.add("permissions")
    productFlavors {
        create("screenshot") {
            dimension = "permissions"
        }
        create("regular") {
            dimension = "permissions"
        }
    }

    compileOptions {
        encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    kotlin {
        jvmToolchain(20)
    }

    bundle {
        language {
            @Suppress("UnstableApiUsage")
            enableSplit = false
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
    }

    ndkVersion = "28.0.13004108"

    //region - Lint
    lint {
        abortOnError = false
        checkReleaseBuilds = true
        warningsAsErrors = true
        checkDependencies = true

        htmlReport = true
        htmlOutput = file("${layout.buildDirectory}/reports/code-quality/lint.html")
        textOutput = file("${layout.buildDirectory}/reports/code-quality/lint.txt")
        disable.add("MissingTranslation")
        enable += setOf(
            "ComposableNaming",
            "ComposableFunctionName",
            "ModifierOrder",
            "UnrememberedMutableState",
        )
    }
    //endregion

    //region - Ktlint
    ktlint {
        android.set(true)
        ignoreFailures.set(true)
        coloredOutput.set(true)
        outputToConsole.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        }
    }
    //endregion
}
//region - Detekt
detekt {
    toolVersion = libs.versions.detekt.get()
    buildUponDefaultConfig = true
    allRules = false
    parallel = true

    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/detekt-baseline.xml")
    basePath = projectDir.path
    ignoreFailures = true
}
//endregion

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        //region - Enable HTML report
        html.required.set(true)
        html.outputLocation.set(file("${layout.buildDirectory.get()}/reports/detekt/detekt.html"))
        //endregion

        //region - Enable XML report (often used for CI)
        xml.required.set(false)
        //xml.outputLocation.set(file("${layout.buildDirectory.get()}/reports/detekt/detekt.xml"))
        //endregion

        //region - Enable SARIF report (best for GitHub Actions)
        sarif.required.set(false)
        //sarif.outputLocation.set(file("${layout.buildDirectory.get()}/reports/detekt/detekt.sarif"))
        //endregion

        //region - Disable TXT report if not needed
        txt.required.set(false)
        //endregion
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.google.material)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.appcompat.resources)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.viewpager)
    implementation(libs.androidx.recyclerview.selection)
    implementation(libs.androidx.recyclerview)
    implementation(libs.bundles.camerax)
    implementation(libs.commons.io)
    implementation(libs.zxing.android.embedded)
    implementation(libs.apache.poi)
    implementation(libs.jlhttp)
    implementation(libs.caverock.androidsvg)
    implementation(libs.paho.mqtt.android)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    detektPlugins(libs.detekt.compose)

    //gson + serilization
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)

    add("androidTestScreenshotImplementation", libs.junit)
    add("androidTestScreenshotImplementation", libs.fastlane.screengrab)
    add("androidTestScreenshotImplementation", libs.androidx.test.rules)
    add("androidTestScreenshotImplementation", libs.androidx.test.ext.junit)
    add("androidTestScreenshotImplementation", libs.androidx.test.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.google.truth)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)

    testImplementation(libs.turbine)
    testImplementation(libs.roboelectric)
}

tasks.register("lintAll") {
    group = "verification"
    description = "Run ktlint, detekt, and Android Lint (debug + release)"

    dependsOn(
        "ktlintCheck",
        "detekt",
        "lintRegularDebug",
        "lintRegularRelease",
    )
}

tasks.named("check") {
    dependsOn("lintAll")
}
