package com.exemplo.camadas.controller;

import com.exemplo.camadas.config.ServidorWebConfig;
import com.exemplo.camadas.dto.ContextoPaginaDTO;
import com.exemplo.camadas.dto.ProdutoRequestDTO;
import com.exemplo.camadas.dto.ProdutoResponseDTO;
import com.exemplo.camadas.handler.GlobalErrorHandler;
import com.exemplo.camadas.service.IProdutoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProdutoController implements HttpHandler {

    private final IProdutoService produtoService;
    private final GlobalErrorHandler globalErrorHandler;
    private final ServidorWebConfig servidorWebConfig;

    public ProdutoController(
            IProdutoService produtoService,
            GlobalErrorHandler globalErrorHandler,
            ServidorWebConfig servidorWebConfig
    ) {
        this.produtoService = produtoService;
        this.globalErrorHandler = globalErrorHandler;
        this.servidorWebConfig = servidorWebConfig;
    }

    @Override
    public void handle(HttpExchange requisicaoHttp) throws IOException {
        try {
            rotearRequisicaoHttp(requisicaoHttp);
        } catch (Exception excecaoCapturada) {
            ContextoPaginaDTO contextoDeErro = globalErrorHandler.capturar(excecaoCapturada);
            servidorWebConfig.enviarPaginaHtml(requisicaoHttp, contextoDeErro);
        }
    }

    private void rotearRequisicaoHttp(HttpExchange requisicaoHttp) throws IOException {
        String metodoHttp = requisicaoHttp.getRequestMethod();
        String caminhoDaRota = requisicaoHttp.getRequestURI().getPath();

        if ("GET".equalsIgnoreCase(metodoHttp) && caminhoDaRota.endsWith("estilos.css")) {
            servidorWebConfig.enviarFolhaDeEstiloCss(requisicaoHttp);
            return;
        }
        if ("GET".equalsIgnoreCase(metodoHttp) && ("/produtos".equals(caminhoDaRota) || "/".equals(caminhoDaRota))) {
            processarListagemDeProdutos(requisicaoHttp);
            return;
        }
        if ("POST".equalsIgnoreCase(metodoHttp) && "/produtos/salvar".equals(caminhoDaRota)) {
            processarSalvamentoDeProduto(requisicaoHttp);
            return;
        }
        if ("POST".equalsIgnoreCase(metodoHttp) && "/produtos/remover".equals(caminhoDaRota)) {
            processarRemocaoDeProduto(requisicaoHttp);
            return;
        }
        servidorWebConfig.redirecionarParaRota(requisicaoHttp, "/produtos");
    }

    private void processarListagemDeProdutos(HttpExchange requisicaoHttp) throws IOException {
        Map<String, String> parametrosDaUrl = ProdutoRequestDTO.extrairMapaDeParametros(
                requisicaoHttp.getRequestURI().getRawQuery()
        );
        Long identificadorParaEdicao = ProdutoRequestDTO.converterTextoParaId(parametrosDaUrl.get("editarId"));
        ProdutoResponseDTO produtoEmEdicao = identificadorParaEdicao != null
                ? produtoService.buscarPorId(identificadorParaEdicao)
                : null;
        ContextoPaginaDTO contextoDaPagina = new ContextoPaginaDTO(
                produtoService.listarTodos(), produtoEmEdicao, null, parametrosDaUrl.get("sucesso"), 200
        );
        servidorWebConfig.enviarPaginaHtml(requisicaoHttp, contextoDaPagina);
    }

    private void processarSalvamentoDeProduto(HttpExchange requisicaoHttp) throws IOException {
        String corpoFormulario = lerCorpoDaRequisicao(requisicaoHttp);
        ProdutoRequestDTO dadosRequisicaoProduto = ProdutoRequestDTO.criarAPartirDeFormularioUrlEncoded(corpoFormulario);
        if (dadosRequisicaoProduto.id() == null) {
            produtoService.cadastrar(dadosRequisicaoProduto);
            servidorWebConfig.redirecionarComMensagemDeSucesso(requisicaoHttp, "Produto cadastrado com sucesso!");
            return;
        }
        produtoService.atualizar(dadosRequisicaoProduto);
        servidorWebConfig.redirecionarComMensagemDeSucesso(requisicaoHttp, "Produto atualizado com sucesso!");
    }

    private void processarRemocaoDeProduto(HttpExchange requisicaoHttp) throws IOException {
        Map<String, String> camposDoFormulario = ProdutoRequestDTO.extrairMapaDeParametros(lerCorpoDaRequisicao(requisicaoHttp));
        Long identificadorParaRemover = ProdutoRequestDTO.converterTextoParaId(camposDoFormulario.get("id"));
        produtoService.remover(identificadorParaRemover);
        servidorWebConfig.redirecionarComMensagemDeSucesso(requisicaoHttp, "Produto removido com sucesso!");
    }

    private String lerCorpoDaRequisicao(HttpExchange requisicaoHttp) throws IOException {
        return new String(requisicaoHttp.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
