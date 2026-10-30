package com.exemplo.camadas.model;

import com.exemplo.camadas.exception.DomainValidationException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    protected Cliente() {
    }

    public Cliente(Long id, String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new DomainValidationException("O nome do cliente é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("O e-mail do cliente é obrigatório.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.email = email.trim();
    }

    public void adicionarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new DomainValidationException("O pedido não pode ser nulo.");
        }
        this.pedidos.add(pedido);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public List<Pedido> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }
}
