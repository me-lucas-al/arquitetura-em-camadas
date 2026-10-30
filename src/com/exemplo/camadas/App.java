package com.exemplo.camadas;

import com.exemplo.camadas.config.ServidorWebConfig;
import com.exemplo.camadas.controller.ProdutoController;
import com.exemplo.camadas.handler.GlobalErrorHandler;
import com.exemplo.camadas.mapper.ProdutoMapper;
import com.exemplo.camadas.repository.IProdutoRepository;
import com.exemplo.camadas.repository.ProdutoRepository;
import com.exemplo.camadas.service.IProdutoService;
import com.exemplo.camadas.service.ProdutoService;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        IProdutoRepository produtoRepository = new ProdutoRepository();
        ProdutoMapper produtoMapper = new ProdutoMapper();
        IProdutoService produtoService = new ProdutoService(produtoRepository, produtoMapper);
        GlobalErrorHandler globalErrorHandler = new GlobalErrorHandler(produtoService);
        ServidorWebConfig servidorWebConfig = new ServidorWebConfig();
        ProdutoController produtoController = new ProdutoController(
                produtoService,
                globalErrorHandler,
                servidorWebConfig
        );

        servidorWebConfig.iniciarServidorHttp(produtoController);
    }
}
