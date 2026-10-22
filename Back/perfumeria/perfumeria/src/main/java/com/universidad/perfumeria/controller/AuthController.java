package com.universidad.perfumeria.controller;

import com.universidad.perfumeria.controller.dto.JwtResponse;
import com.universidad.perfumeria.controller.dto.LoginRequest;
import com.universidad.perfumeria.security.JwtUtils;
import com.universidad.perfumeria.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Spring Security verifica si el usuario y contraseña coinciden en la BD
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Si es correcto, lo guardamos en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 3. Generamos la "llave" JWT
        String jwt = jwtUtils.generarTokenJwt(authentication);

        // 4. Obtenemos los datos del usuario para devolverlos a Angular
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 5. Enviamos la respuesta exitosa
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
    }
}