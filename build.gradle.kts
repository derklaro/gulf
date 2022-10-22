import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
  id("signing")
  id("checkstyle")
  id("java-library")
  id("maven-publish")
  alias(libs.plugins.spotless)
  alias(libs.plugins.nexusPublish)
}

defaultTasks("build", "test", "jar")

version = "1.0.0-SNAPSHOT"
group = "dev.derklaro.gulf"

repositories {
  mavenCentral()
}

dependencies {
  // lombok
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)
  // general
  compileOnly(libs.annotations)
  // testing
  testImplementation(libs.bundles.junit)
}

tasks.withType<JavaCompile> {
  sourceCompatibility = JavaVersion.VERSION_1_8.toString()
  targetCompatibility = JavaVersion.VERSION_1_8.toString()
  // options
  options.encoding = "UTF-8"
  options.isIncremental = true
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}

tasks.withType<Checkstyle> {
  maxErrors = 0
  maxWarnings = 0
  configFile = rootProject.file("checkstyle.xml")
}

extensions.configure<SpotlessExtension> {
  java {
    licenseHeaderFile(file("license_header.txt"))
  }
}

extensions.configure<JavaPluginExtension> {
  withSourcesJar()
  withJavadocJar()
}

extensions.configure<CheckstyleExtension> {
  toolVersion = "10.3.4"
}

extensions.configure<PublishingExtension> {
  publications {
    create("library", MavenPublication::class.java) {
      from(project.components.getByName("java"))

      pom {
        name.set(project.name)
        url.set("https://github.com/derklaro/gulf")
        description.set("A small library to compare java objects")

        licenses {
          license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
          }
        }

        developers {
          developer {
            name.set("Pasqual Koschmieder")
            email.set("git@derklaro.dev")
          }
        }

        scm {
          url.set("https://github.com/derklaro/gulf")
          connection.set("https://github.com/derklaro/gulf.git")
        }

        issueManagement {
          system.set("GitHub Issues")
          url.set("https://github.com/derklaro/gulf/issues")
        }

        withXml {
          val repositories = asNode().appendNode("repositories")
          project.repositories.forEach {
            if (it is MavenArtifactRepository && it.url.toString().startsWith("https://")) {
              val repo = repositories.appendNode("repository")
              repo.appendNode("id", it.name)
              repo.appendNode("url", it.url.toString())
            }
          }
        }
      }
    }
  }
}

tasks.withType<Sign> {
  onlyIf {
    !project.rootProject.version.toString().endsWith("-SNAPSHOT")
  }
}

extensions.configure<SigningExtension> {
  useGpgCmd()
  sign(extensions.getByType(PublishingExtension::class.java).publications["library"])
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

      username.set(project.findProperty("ossrhUsername")?.toString() ?: "")
      password.set(project.findProperty("ossrhPassword")?.toString() ?: "")
    }
  }

  useStaging.set(!project.rootProject.version.toString().endsWith("-SNAPSHOT"))
}
