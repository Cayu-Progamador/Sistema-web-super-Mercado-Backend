package com.backendSupermercado.supermercasdo.modules.kardex.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexFiltroDto;
import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexResponseDto;

public interface KardexService {
    Page<KardexResponseDto> listar(KardexFiltroDto filtros, Pageable pageable);

    byte[] exportarPdf(KardexFiltroDto filtros);
}
