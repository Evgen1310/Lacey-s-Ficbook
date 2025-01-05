package ru.yarsu.config

import org.http4k.config.Environment
import org.http4k.config.EnvironmentKey
import org.http4k.lens.nonEmptyString

class DatabaseConfig(
    val authSalt: String,
    val jwtSecret: String,
) {
    companion object : Config {
        private val authSaltLens = EnvironmentKey.nonEmptyString().required("auth.salt")
        private val jwtSecretLens = EnvironmentKey.nonEmptyString().required("jwt.secret")
        override val defaultEnv =
            Environment.defaults(
                authSaltLens of "",
                jwtSecretLens of "",
            )

        fun createDatabaseConfig(env: Environment): DatabaseConfig {
            return DatabaseConfig(
                authSaltLens(env),
                jwtSecretLens(env),
            )
        }
    }
}
