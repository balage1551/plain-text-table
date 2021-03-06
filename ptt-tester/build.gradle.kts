import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib"))
    implementation(project(":ptt-core"))
}

tasks.withType<Test> {
    useTestNG()
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}









//
//// project information
////archivesBaseName=rootProject.name+"-"+project.name+"-"+project.version
//
//// dependency management as you like
//repositories {
//    mavenCentral()
//}
//
//// javadoc.jar generation
//task javadocJar (type: Jar, dependsOn: javadoc) {
//    classifier = 'javadoc'
//    from javadoc.destinationDir
//}
//// sources.jar generation
//task sourceJar (type : Jar) {
//    classifier = 'sources'
//    from sourceSets.main.allSource
//}
//
//// summarize artifacts
//artifacts {
//    archives sourceJar
//            archives javadocJar
//}
//
//signing {
//    sign configurations.archives
//}
//
//uploadArchives {
//    repositories {
//        mavenDeployer {
//            // POM signature
//            beforeDeployment { MavenDeployment deployment -> signing.signPom(deployment) }
//
//
//            // Target repository
//            repository(url: "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
//            authentication(userName: project.properties.ossrhUser, password: project.properties.ossrhPassword)
//        }
//
//            snapshotRepository(url: "https://oss.sonatype.org/content/repositories/snapshots/") {
//            authentication(userName: project.properties.ossrhUser, password: project.properties.ossrhPassword)
//        }
//
//            pom.project {
//                name project.name
//                        description 'A customizable plain-text based table generator'
//                packaging 'jar'
//                url 'https://github.com/balage1551/plain-text-table'
//
//                scm {
//                    url 'https://github.com/balage1551/plain-text-table/tree/master'
//                    connection 'scm:git:git://github.com/balage1551/plain-text-table.git'
//                    developerConnection 'scm:git:ssh://github.com/balage1551/plain-text-table.git'
//                }
//                licenses {
//                    license {
//                        name 'The Apache Software License, Version 2.0'
//                        url 'http://www.apache.org/license/LICENSE-2.0.txt'
//                        distribution 'repo'
//                    }
//                }
//
//                developers {
//                    developer {
//                        id 'Balage'
//                        name 'Balázs Vissy'
//                        email 'balage42-maven@yahoo.com'
//                    }
//                }
//            }
//        }
//    }
//
//    uploadArchives.finalizedBy 'updateGitIfReleaseSuccessful'
//
//    uploadArchives.doFirst {
//        logger.quiet("Uploading archives to version: ${project.version}")
//    }
//}
//
//
//
//gradle.taskGraph.whenReady {
//    if (gradle.taskGraph.hasTask(":${project.name}:uploadArchives") ||
//            gradle.taskGraph.hasTask(":${project.name}:uploadTest")) {
//        rootProject.ext.versionUtils = new VersionUtils()
//        rootProject.versionUtils.validateGitState()
//        ext.lv = rootProject.versionUtils.getLastRelease()
//        ext.nv = rootProject.versionUtils.getNewRelease()
//        rootProject.version=nv.toVersionSequence()
//        project.version=nv.toVersionSequence()
//
//        logger.quiet("Last version: ${lv.toVersionSequence()}")
//        logger.quiet("New version: ${nv.toVersionSequence()}")
//        rootProject.versionUtils.updateChangeLog()
//    } else {
//    }
//}
//
//
//task uploadTest(dependsOn: assemble) {
//
//    finalizedBy 'updateGitIfReleaseSuccessful'
//
//    doLast {
//        logger.info("Version: ${project.version}")
//        rootProject.versionUtils.simulateUpload()
//    }
//}
//
//task updateGitIfReleaseSuccessful {
//    onlyIf {
//        uploadTest.state.failure == null
//    }
//
//    doLast {
//        logger.lifecycle("Updating git to version: ${rootProject.version}")
//        def v = rootProject.versionUtils.getNewRelease()
//        rootProject.versionUtils.updateRepository()
//    }
//}
