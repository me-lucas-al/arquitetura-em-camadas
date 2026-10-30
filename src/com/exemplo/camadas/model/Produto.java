package com.exemplo.camadas.model;

import com.exemplo.camadas.exception.DomainValidationException;
import com.exemplo.camadas.valueobject.NomeProduto;
import com.exemplo.camadas.valueobject.Preco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private NomeProduto nome;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private Preco preco;

    protected Produto() {
    }

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
