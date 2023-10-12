import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
}

group = "com.sannimith"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}
val sqlLightVersion = "1.5.5"
sqldelight {
    database("Database") {
        packageName = "com.sannimith.khmerdictionary"
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)

    implementation("com.squareup.sqldelight:runtime:$sqlLightVersion")
    implementation("com.squareup.sqldelight:sqlite-driver:$sqlLightVersion")
    implementation("com.squareup.sqldelight:coroutines-extensions:$sqlLightVersion")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.6")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KhmerDictionary"
            packageVersion = "1.0.0"
        }
    }
}
