package com.exemplo.camadas.model;

import com.exemplo.camadas.exception.DomainValidationException;
import com.exemplo.camadas.valueobject.NomeProduto;
import com.exemplo.camadas.valueobject.Preco;

public class Produto {

    private final Long id;
    private final NomeProduto nome;
    private final Preco preco;

    public Produto(NomeProduto nome, Preco preco) {
        this(null, nome, preco);
    }

    public Produto(Long id, NomeProduto nome, Preco preco) {
        if (nome == null) {
            throw new DomainValidationException("O nome é obrigatório.");
        }
        if (preco == null) {
            throw new DomainValidationException("O preço é obrigatório.");
        }
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    public Produto comId(Long novoId) {
        return new Produto(novoId, this.nome, this.preco);
    }

    public Long getId() {
        return id;
    }

    public NomeProduto getNome() {
        return nome;
    }

    public Preco getPreco() {
        return preco;
    }
}
