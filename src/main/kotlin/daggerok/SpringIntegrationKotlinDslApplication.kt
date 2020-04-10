package daggerok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.MessageChannels
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.file.dsl.Files
import java.io.File

@Configuration
class ChannelsConfig {
  @Bean fun txt() = MessageChannels.direct().get()
  @Bean fun csv() = MessageChannels.direct().get()
  @Bean fun errors() = MessageChannels.direct().get()
}

@Configuration
class FileConfiguration(private val channelsConfig: ChannelsConfig) {

  private val isCI = "true" == System.getenv("CI") ?: "false"
  private val base = if (!isCI) File("./target") else File("/tmp/target")
    // else java.nio.file.Files.createTempDirectory("target").toFile()
  private val inputs = File(base, "inputs")
  private val output = File(base, "output")
  private val txtOutput = File(output, "txt")
  private val csvOutput = File(output, "csv")

  @Bean fun inputFilesFlow() = integrationFlow(
      Files.inboundAdapter(inputs).autoCreateDirectory(true),
      { poller { it.fixedDelay(5000).maxMessagesPerPoll(1) } }
  ) {
    filter<File> { it.isFile }
    route<File> {
      when (it.extension.toLowerCase()) {
        "txt" -> channelsConfig.txt()
        "csv" -> channelsConfig.csv()
        else -> channelsConfig.errors()
      }
    }
  }

  @Bean fun txtFilesFlow() = integrationFlow(channelsConfig.txt()) {
    handle(Files.outboundAdapter(txtOutput).autoCreateDirectory(true))
  }

  @Bean fun csvFilesFlow() = integrationFlow(channelsConfig.csv()) {
    handle(Files.outboundAdapter(csvOutput).autoCreateDirectory(true))
  }
}

@SpringBootApplication
class SpringIntegrationKotlinDslApplication

fun main(args: Array<String>) {
  runApplication<SpringIntegrationKotlinDslApplication>(*args)
}
