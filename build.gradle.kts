import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    java
    idea
}

group = "hu.vissy"
version = "1.1"

buildscript {

    repositories {
        jcenter()
        mavenCentral()
    }

}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        jcenter()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}


dependencies {
    compile(kotlin("stdlib"))

    api(project(":ptt-core"))
    api(project(":ptt-tester"))

    testCompile("org.testng:testng:6.14.3")
}

tasks.withType<Test> {
    useTestNG()
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

