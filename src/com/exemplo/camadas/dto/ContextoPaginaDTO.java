package com.exemplo.camadas.dto;

import java.util.List;

public record ContextoPaginaDTO(
        List<ProdutoResponseDTO> produtos,
        ProdutoResponseDTO produtoEmEdicao,
        String erro,
        String sucesso,
        int statusHttp
) {
}
