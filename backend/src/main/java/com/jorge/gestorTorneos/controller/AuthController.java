package com.jorge.gestorTorneos.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.jorge.gestorTorneos.dto.request.LoginRequest;
import com.jorge.gestorTorneos.dto.request.RegisterRequest;
import com.jorge.gestorTorneos.dto.response.LoginResponse;
import com.jorge.gestorTorneos.dto.response.UsuarioResponse;
import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public UsuarioResponse register(@RequestBody RegisterRequest request) {
        return UsuarioResponse.fromEntity(authService.register(request));
    }

    @GetMapping("/me")
    public LoginResponse me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extraerBearerToken(authorization);
        return authService.mePorToken(token);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extraerBearerToken(authorization);
        authService.logout(token);
        return Map.of("message", "Logout realizado correctamente");
    }

    private static String extraerBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BadRequestException("Token no enviado");
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new BadRequestException("Token inválido");
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new BadRequestException("Token no enviado");
        }
        return token;
    }
}
