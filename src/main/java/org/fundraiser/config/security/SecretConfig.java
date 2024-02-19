package org.fundraiser.config.security;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretConfig {
    private static final String SECRET = "tydQLQsX9^&Wgr";

    public static String getSecret() {
        return SECRET;
    }
}