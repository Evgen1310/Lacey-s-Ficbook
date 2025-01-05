package ru.yarsu.web.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import java.sql.Date
import java.time.LocalDate

class JwtTools(
    private val jwtSalt: String,
    private val orgName: String,
    private val defaultExpireTime: Int,
) {
    private val algorithm = Algorithm.HMAC512(jwtSalt)
    private val verifier =
        JWT.require(algorithm)
            .withIssuer(orgName)
            .build()

    fun buildToken(id: String): String? {
        return try {
            JWT.create()
                .withSubject(id)
                .withExpiresAt(Date.valueOf(LocalDate.now().plusDays(defaultExpireTime.toLong())))
                .withIssuer(orgName)
                .sign(algorithm)
        } catch (exception: JWTCreationException) {
            null
        }
    }

    fun checkToken(token: String?): String? {
        return try {
            val decodedJwt: DecodedJWT = verifier.verify(token)
            decodedJwt.subject
        } catch (exception: JWTVerificationException) {
            null
        }
    }
}
