pluginManagement {
  val jibVersion: String by extra
  val kotlinVersion: String by extra
  val reckonVersion: String by extra
  val palantirVersion: String by extra
  val springBootVersion: String by extra
  val versionsPluginVersion: String by extra
  val dependencyManagementVersion: String by extra
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.ajoberstar.reckon") version reckonVersion
    id("com.palantir.docker-run") version palantirVersion
    id("org.springframework.boot") version springBootVersion
    id("com.google.cloud.tools.jib") version jibVersion
    id("com.github.ben-manes.versions") version versionsPluginVersion
    id("io.spring.dependency-management") version dependencyManagementVersion
  }
  repositories {
    gradlePluginPortal()
  }
}

val name: String by extra
rootProject.name = name
