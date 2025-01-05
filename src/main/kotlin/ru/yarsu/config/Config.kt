package ru.yarsu.config

import org.http4k.config.Environment

interface Config {
    val defaultEnv: Environment
}
