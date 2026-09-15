package com.universidad.perfumeria;

import com.universidad.perfumeria.controller.PerfumeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PerfumeriaApplicationTests {

    @Autowired
    private PerfumeController perfumeController;

    @Test
    void contextLoads() {
        // Verifica que el contenedor de Spring inyecte el controlador correctamente
        assertNotNull(perfumeController, "El controlador de perfumes no debe ser nulo");
    }

}