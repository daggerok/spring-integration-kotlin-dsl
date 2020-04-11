# Spring Integration Kotlin DSL [![CI](https://github.com/daggerok/spring-integration-kotlin-dsl/workflows/CI/badge.svg)](https://github.com/daggerok/spring-integration-kotlin-dsl/actions?query=workflow%3ACI)

<!--

# Read Me First
The following was discovered as part of building this project:

* The JVM level was changed from '14' to '11', review the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range) on the wiki for more details.

# Getting Started

-->

## build, run, test

```bash
./gradlew clean build jibDockerBuild
./gradlew dockerRun -P demonize=true
docker logs -f -t spring-integration-kotlin-dsl &
docker exec -i spring-integration-kotlin-dsl bash -c 'echo "ololo trololo" > /tmp/target/inputs/first-file.txt'
docker exec -i spring-integration-kotlin-dsl bash -c 'echo "trololo|ololo" > /tmp/target/inputs/second-file.csv'
./gradlew dockerStop
```

## versions

_git remote -> https_

```bash
./gradlew reckonTagPush -Preckon.stage=final \
  -Dorg.ajoberstar.grgit.auth.username=daggerok \
  -Dorg.ajoberstar.grgit.auth.password=...
```

_git remote -> git_

```bash
./gradlew reckonTagPush -Preckon.stage=final
```

## other repositories
* [GitHub: daggerok/spring-integration-java-DSL-example](https://github.com/daggerok/spring-integration-java-DSL-example)
* [GitHub: daggerok/spring-integration-5-examples](https://github.com/daggerok/spring-integration-5-examples)

## resources
* https://www.youtube.com/watch?v=A9eDlMSTuE0
* https://www.youtube.com/watch?v=1llAwgOKjTY
* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/gradle-plugin/reference/html/)
* [Coroutines section of the Spring Framework Documentation](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/languages.html#coroutines)
* [Spring Integration](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-integration)
* [Integrating Data](https://spring.io/guides/gs/integration/)
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
