package com.universidad.perfumeria.repository;

import com.universidad.perfumeria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método clave para que Spring Security busque al usuario al iniciar sesión
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
}