package com.jorge.gestorTorneos.service;

import java.time.OffsetDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jorge.gestorTorneos.dto.request.LoginRequest;
import com.jorge.gestorTorneos.dto.request.RegisterRequest;
import com.jorge.gestorTorneos.dto.response.LoginResponse;
import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ConflictException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SesionService sesionService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, SesionService sesionService,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.sesionService = sesionService;
        this.passwordEncoder = passwordEncoder;
    }
    
    private void validarPasswordSegura(String password) {
        if (password == null || password.length() < 8) {
            throw new BadRequestException("La contraseña debe tener al menos 8 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("La contraseña debe incluir al menos una mayúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("La contraseña debe incluir al menos una minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            throw new BadRequestException("La contraseña debe incluir al menos un número");
        }
    }

    public Usuario register(RegisterRequest request) {
    	validarPasswordSegura(request.getPassword());
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Ya existe un usuario con ese email");
        }
        
        

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaRegistro(OffsetDateTime.now());
        usuario.setRol(RolUsuario.USUARIO);
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("El email es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("La password es obligatoria");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("Usuario desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new BadRequestException("Credenciales incorrectas");
        }

        String token = sesionService.crearSesion(usuario.getId());
        return LoginResponse.fromEntity(usuario, token);
    }

    public LoginResponse mePorToken(String token) {
        if (!sesionService.tokenValido(token)) {
            throw new BadRequestException("Token inválido");
        }
        Integer usuarioId = sesionService.obtenerUsuarioIdPorToken(token);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return LoginResponse.fromEntity(usuario, token);
    }

    public void logout(String token) {
        if (!sesionService.tokenValido(token)) {
            throw new BadRequestException("Token inválido");
        }
        sesionService.cerrarSesion(token);
    }
}
