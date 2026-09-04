package com.backendSupermercado.supermercasdo.modules.inventario_fisico.exception;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;

public class InventarioFisicoConflictoException extends ResourceConflictException {
    public InventarioFisicoConflictoException(String mensaje) {
        super(mensaje);
    }
}
