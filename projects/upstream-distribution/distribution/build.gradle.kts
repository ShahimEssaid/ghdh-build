plugins {
    id("application")
}

repositories{
    mavenCentral()
}

dependencies {
    implementation("com.google.cloud.verticals.foundations.dataharmonization:runtime:${version}")
}

application {
    mainClass.set("com.google.cloud.verticals.foundations.dataharmonization.Main")
}
