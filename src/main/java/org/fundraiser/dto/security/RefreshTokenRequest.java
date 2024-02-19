package org.fundraiser.dto.security;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequest {

    private String refreshToken;

}
