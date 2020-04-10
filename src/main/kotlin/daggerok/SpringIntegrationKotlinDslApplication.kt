package daggerok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringIntegrationKotlinDslApplication

fun main(args: Array<String>) {
  runApplication<SpringIntegrationKotlinDslApplication>(*args)
}
