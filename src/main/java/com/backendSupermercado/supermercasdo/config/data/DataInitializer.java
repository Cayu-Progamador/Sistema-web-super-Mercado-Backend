package com.backendSupermercado.supermercasdo.config.data;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.TipoUnidadMedida;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.repository.TipoUnidadMedidaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TipoUnidadMedidaRepository tipoUnidadMedidaRepository;

    @Override
    public void run(String... args) {
        List<String> tipos = List.of("Peso", "Volumen", "Conteo", "Lineal");
        tipos.forEach(nombre -> {
            if (!tipoUnidadMedidaRepository.existsByNombreIgnoreCase(nombre)) {
                tipoUnidadMedidaRepository.save(TipoUnidadMedida.builder().nombre(nombre).build());
            }
        });
    }
}