package com.ndinhchien.m4y.domain.auth.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPayload {
    private final String type;
    private final String email;
    private final String role;
}
