package com.exemplo.camadas.config;

import com.exemplo.camadas.dto.ContextoPaginaDTO;
import com.exemplo.camadas.dto.ProdutoResponseDTO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ServidorWebConfig {

    private static final int PORTA_SERVIDOR = 8080;
    private static final Path CAMINHO_ARQUIVO_HTML = Path.of("src", "com", "exemplo", "camadas", "view", "produtos.html");
    private static final Path CAMINHO_ARQUIVO_CSS = Path.of("src", "com", "exemplo", "camadas", "view", "estilos.css");

    public void iniciarServidorHttp(HttpHandler controladorPrincipal) throws IOException {
        HttpServer servidorHttp = HttpServer.create(new InetSocketAddress(PORTA_SERVIDOR), 0);
        servidorHttp.createContext("/", controladorPrincipal);
        servidorHttp.setExecutor(null);
        servidorHttp.start();
        System.out.println("Servidor iniciado em http://localhost:" + PORTA_SERVIDOR + "/produtos");
    }

    public void enviarPaginaHtml(HttpExchange requisicaoHttp, ContextoPaginaDTO contextoDaPagina) throws IOException {
        String conteudoHtmlBase = Files.readString(CAMINHO_ARQUIVO_HTML, StandardCharsets.UTF_8);
        String conteudoHtmlRenderizado = conteudoHtmlBase.replace(
                "const ESTADO_DA_PAGINA = { produtos: [], emEdicao: null, erro: null, sucesso: null };",
                "const ESTADO_DA_PAGINA = " + serializarContextoDaPaginaEmJson(contextoDaPagina) + ";"
        );
        byte[] bytesDaResposta = conteudoHtmlRenderizado.getBytes(StandardCharsets.UTF_8);
        requisicaoHttp.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        requisicaoHttp.sendResponseHeaders(contextoDaPagina.statusHttp(), bytesDaResposta.length);
        requisicaoHttp.getResponseBody().write(bytesDaResposta);
        requisicaoHttp.close();
    }

    public void enviarFolhaDeEstiloCss(HttpExchange requisicaoHttp) throws IOException {
        byte[] bytesDoCss = Files.readString(CAMINHO_ARQUIVO_CSS, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
        requisicaoHttp.getResponseHeaders().set("Content-Type", "text/css; charset=UTF-8");
        requisicaoHttp.sendResponseHeaders(200, bytesDoCss.length);
        requisicaoHttp.getResponseBody().write(bytesDoCss);
        requisicaoHttp.close();
    }

    public void redirecionarComMensagemDeSucesso(HttpExchange requisicaoHttp, String mensagemDeSucesso) throws IOException {
        String mensagemCodificadaNaUrl = URLEncoder.encode(mensagemDeSucesso, StandardCharsets.UTF_8);
        redirecionarParaRota(requisicaoHttp, "/produtos?sucesso=" + mensagemCodificadaNaUrl);
    }

    public void redirecionarParaRota(HttpExchange requisicaoHttp, String rotaDeDestino) throws IOException {
        requisicaoHttp.getResponseHeaders().set("Location", rotaDeDestino);
        requisicaoHttp.sendResponseHeaders(303, -1);
        requisicaoHttp.close();
    }

    private String serializarContextoDaPaginaEmJson(ContextoPaginaDTO contextoDaPagina) {
        return "{\"produtos\":" + serializarListaDeProdutosEmJson(contextoDaPagina.produtos())
                + ",\"emEdicao\":" + serializarProdutoEmJson(contextoDaPagina.produtoEmEdicao())
                + ",\"erro\":" + serializarTextoEmJson(contextoDaPagina.erro())
                + ",\"sucesso\":" + serializarTextoEmJson(contextoDaPagina.sucesso()) + "}";
    }

    private String serializarListaDeProdutosEmJson(List<ProdutoResponseDTO> listaDeProdutos) {
        return "[" + listaDeProdutos.stream().map(this::serializarProdutoEmJson).collect(Collectors.joining(",")) + "]";
    }

    private String serializarProdutoEmJson(ProdutoResponseDTO produtoResposta) {
        if (produtoResposta == null) {
            return "null";
        }
        return "{\"id\":" + produtoResposta.id()
                + ",\"nome\":" + serializarTextoEmJson(produtoResposta.nome())
                + ",\"preco\":" + produtoResposta.preco() + "}";
    }

    private String serializarTextoEmJson(String textoOriginal) {
        if (textoOriginal == null || textoOriginal.isBlank()) {
            return "null";
        }
        String textoEscapado = textoOriginal.replace("\\", "\\\\").replace("\"", "\\\"").replace("/", "\\/");
        return "\"" + textoEscapado + "\"";
    }
}
