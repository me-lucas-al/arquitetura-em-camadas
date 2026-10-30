package com.exemplo.camadas.handler;

import com.exemplo.camadas.dto.ContextoPaginaDTO;
import com.exemplo.camadas.exception.DomainValidationException;
import com.exemplo.camadas.service.IProdutoService;

public class GlobalErrorHandler {

    private final IProdutoService produtoService;

    public GlobalErrorHandler(IProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public ContextoPaginaDTO capturar(Exception excecao) {
        if (excecao instanceof DomainValidationException || excecao instanceof IllegalArgumentException) {
            return tratarErroDeValidacao(excecao.getMessage());
        }
        return tratarErroGenerico();
    }

    public ContextoPaginaDTO tratarErroDeValidacao(String mensagem) {
        return new ContextoPaginaDTO(
                produtoService.listarTodos(),
                null,
                mensagem,
                null,
                422
        );
    }

    public ContextoPaginaDTO tratarErroGenerico() {
        return new ContextoPaginaDTO(
                produtoService.listarTodos(),
                null,
                "Ocorreu um erro inesperado ao processar sua solicitação.",
                null,
                500
        );
    }
}
