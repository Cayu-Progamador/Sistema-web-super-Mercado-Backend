package com.backendSupermercado.supermercasdo.modules.inventario_fisico.exception;

import jakarta.persistence.EntityNotFoundException;

public class InventarioFisicoNoEncontradoException extends EntityNotFoundException {
    public InventarioFisicoNoEncontradoException(Long id) {
        super("No existe el inventario físico con id " + id);
    }
}
