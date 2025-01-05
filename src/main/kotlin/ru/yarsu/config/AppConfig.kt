package ru.yarsu.config

import org.http4k.config.Environment
import java.io.File

class AppConfig(
    val webConfig: WebConfig,
    val dbConfig: DatabaseConfig,
)

private fun getAppEnv(config: Config): Environment {
    val file = File("config/app.properties")
    return if (file.exists()) {
        Environment.from(File("config/app.properties")) overrides
            Environment.JVM_PROPERTIES overrides
            Environment.ENV overrides
            config.defaultEnv
    } else {
        Environment.fromResource("app.properties") overrides
            Environment.JVM_PROPERTIES overrides
            Environment.ENV overrides
            config.defaultEnv
    }
}

fun readConfigurations(): AppConfig {
    return AppConfig(
        WebConfig.createWebConfig(getAppEnv(WebConfig)),
        DatabaseConfig.createDatabaseConfig(getAppEnv(DatabaseConfig)),
    )
}
