package ru.yarsu.web.domain.storage

import java.security.MessageDigest
import java.util.HexFormat

class PasswordHandler(
    private val authSalt: String
) {
    fun createPassword(password: String): String {
        val commaFormat = HexFormat.of()
        val saltedBytes = (password + authSalt).toByteArray(charset("UTF-8"))
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(saltedBytes)
        return commaFormat.formatHex(digest)
    }

    fun createNewPasswordOrReturnOld(passwordNew: String, passwordOld: String): String {
        return if (passwordNew.isNotBlank()) {
            createPassword(passwordNew)
        } else {
            passwordOld
        }
    }

    fun checkPassword(
        passwordIn: String,
        passwordOld: String,
    ) = createPassword(passwordIn) == passwordOld
}