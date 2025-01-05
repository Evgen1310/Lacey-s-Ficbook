package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.nonEmptyString

class DatabaseConfig(
    val databasePath: String,
    val authSalt: String,
    val jwtSalt: String,
) {
    companion object : Config {
        private val dataPathLens = EnvironmentKey.nonEmptyString().required("dp.path")
        private val authSaltLens = EnvironmentKey.nonEmptyString().required("auth.salt")
        private val jwtSaltLens = EnvironmentKey.nonEmptyString().required("jwt.salt")
        override val defaultEnv =
            Environment.defaults(
                dataPathLens of "data",
                authSaltLens of "",
                jwtSaltLens of "",
            )

        fun createDatabaseConfig(env: Environment): DatabaseConfig {
            return DatabaseConfig(
                dataPathLens(env),
                authSaltLens(env),
                jwtSaltLens(env),
            )
        }
    }
}
