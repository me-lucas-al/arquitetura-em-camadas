package com.exemplo.camadas.mapper;

import com.exemplo.camadas.dto.ProdutoRequestDTO;
import com.exemplo.camadas.dto.ProdutoResponseDTO;
import com.exemplo.camadas.model.Produto;
import com.exemplo.camadas.valueobject.NomeProduto;
import com.exemplo.camadas.valueobject.Preco;

public class ProdutoMapper {

    public Produto converterParaEntidadeProduto(ProdutoRequestDTO dadosRequisicao) {
        NomeProduto nomeValidado = new NomeProduto(dadosRequisicao.nome());
        Preco precoValidado = new Preco(dadosRequisicao.preco());
        return new Produto(dadosRequisicao.id(), nomeValidado, precoValidado);
    }

    public ProdutoResponseDTO converterParaProdutoResponseDTO(Produto entidadeProduto) {
        return new ProdutoResponseDTO(
                entidadeProduto.getId(),
                entidadeProduto.getNome().getValor(),
                entidadeProduto.getPreco().getValor()
        );
    }
}
