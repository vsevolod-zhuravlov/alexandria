package com.endpoints;

import io.jsonwebtoken.Jwts;
import lombok.Getter;

import javax.crypto.SecretKey;

public class AppSecretKey {
    @Getter
    private static SecretKey secretKey = Jwts.SIG.HS256.key().build();
}
