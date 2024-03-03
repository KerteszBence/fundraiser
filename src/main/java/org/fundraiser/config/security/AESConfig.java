package org.fundraiser.config.security;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "fundraiser-project.aes")
@Setter
public class AESConfig {
    private String secretKey;

    public byte[] getSecretKey() {
        return Base64.getDecoder().decode(secretKey);
    }
}
