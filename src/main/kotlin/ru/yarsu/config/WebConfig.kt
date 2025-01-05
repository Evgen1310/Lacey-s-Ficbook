package ru.yarsu.config

import org.http4k.config.Environment
import org.http4k.config.EnvironmentKey
import org.http4k.lens.int

class WebConfig(
    val webPort: Int,
) {
    companion object : Config {
        private val webPortLens = EnvironmentKey.int().required("web.port", "Application web port")
        override val defaultEnv =
            Environment.defaults(
                webPortLens of 9000,
            )

        fun createWebConfig(env: Environment): WebConfig {
            return WebConfig(webPortLens(env))
        }
    }
}
