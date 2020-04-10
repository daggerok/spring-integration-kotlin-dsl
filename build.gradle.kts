plugins {
  idea
  kotlin("jvm")
  kotlin("plugin.spring")
  id("org.ajoberstar.reckon")
  id("com.palantir.docker-run")
  id("org.springframework.boot")
  id("com.google.cloud.tools.jib")
  id("com.github.ben-manes.versions")
  id("io.spring.dependency-management")
}

val dockerUser: String by project
val dockerImage: String by project
val gradleWrapperVersion: String by project
val `jackson-module-kotlin`: String by project
val `kotlinx-coroutines-reactor`: String by project
val kotlinJvmTargetVersion = JavaVersion.VERSION_13.toString()
val javaVersion = JavaVersion.VERSION_14 // val javaVersion = JavaVersion.VERSION_11

idea {
  project {
    languageLevel = org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(javaVersion)
  }
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

java.sourceCompatibility = javaVersion
java.targetCompatibility = javaVersion

repositories {
  mavenCentral()
  maven(url = uri("https://repo.spring.io/milestone"))
}

dependencies {
  implementation(kotlin("reflect"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(enforcedPlatform("com.fasterxml.jackson.module:jackson-module-kotlin:${`jackson-module-kotlin`}"))
  implementation(enforcedPlatform("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${`kotlinx-coroutines-reactor`}"))
  implementation("org.springframework.integration:spring-integration-file")
  implementation("org.springframework.boot:spring-boot-starter-integration")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.integration:spring-integration-test")
  testImplementation("org.springframework.boot:spring-boot-starter-test")/* { exclude(group = "org.junit.vintage", module = "junit-vintage-engine") }*/
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = kotlinJvmTargetVersion // JavaVersion.VERSION_13.toString() // jvmTarget = javaVersion.toString() // jvmTarget = "1.8"
    }
  }
  withType<Test> {
    useJUnitPlatform()
    testLogging {
      showCauses = true
      showExceptions = true
      showStackTraces = true
      showStandardStreams = true
    }
  }
  withType(Wrapper::class.java) {
    gradleVersion = gradleWrapperVersion
    distributionType = Wrapper.DistributionType.BIN
    // distributionType = Wrapper.DistributionType.ALL
  }
}

springBoot {
  buildInfo()
}

tasks {
  withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    launchScript()
  }
}

reckon {
  scopeFromProp()
  snapshotFromProp() // stageFromProp()
}

tasks {
  register("version") {
    println(project.version.toString())
  }
  register("status") {
    doLast {
      val status = grgit.status()
      status?.let { s ->
        println("workspace is clean: ${s.isClean}")
        if (!s.isClean) {
          if (s.unstaged.allChanges.isNotEmpty()) {
            println("""all unstaged changes: ${s.unstaged.allChanges.joinToString(separator = "") { i -> "\n - $i" }}""")
          }
        }
      }
    }
  }
}

jib {
  from {
    image = dockerImage
  }
  to {
    image = "$dockerUser/${project.name}"
  }
  container {
    environment = mapOf("CI" to "true")
    user = "nobody:nogroup" // depends on base image in line 94!
  }
}

tasks {
  jib { // reckon version pass in jib tags workaround
    this.to.tags = setOf(project.version.toString())
  }
}

defaultTasks("clean", "build")

fun Project.getOrUndefined(envVar: String, sysProp: String, defaultValue: String = "undefined") =
    System.getenv(envVar.toUpperCase())
        ?: project.findProperty(sysProp)
        ?: defaultValue

dockerRun {
  clean = true
  name = project.name
  ports("8080:8080")
  image = "$dockerUser/${project.name}"
  daemonize = project.getOrUndefined("DEMONIZE", "demonize", "false") != "false"
}

/*
tasks {
  val dockerRunTask = dockerRun.get()
  val dockerRemoveContainerTask = dockerRemoveContainer.get()
  dockerRunTask.dependsOn(dockerRemoveContainerTask.path)
}
*/

/*
typealias Os = org.apache.tools.ant.taskdefs.condition.Os

tasks {
  val dockerRunTask = dockerRun.get()
  dockerRunTask.dependsOn("dockerRm")
  dockerRunTask.shouldRunAfter("dockerRm")
  register<Exec>("dockerRm") {
    isIgnoreExitValue = true
    val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)
    if (isWindows) commandLine("cmd", "/c", "docker rm -f -v ${project.name}")
    else commandLine("sh", "-c", "docker rm -f -v `docker ps -aq`")
  }
}
*/
