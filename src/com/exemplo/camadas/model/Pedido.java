package com.exemplo.camadas.model;

import com.exemplo.camadas.exception.DomainValidationException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "PEDIDO")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(Long id, Cliente cliente, LocalDateTime dataPedido) {
        if (cliente == null) {
            throw new DomainValidationException("O cliente do pedido é obrigatório.");
        }
        this.id = id;
        this.cliente = cliente;
        this.dataPedido = dataPedido != null ? dataPedido : LocalDateTime.now();
    }

    public void adicionarItem(Produto produto, int quantidade) {
        ItemPedido novoItem = new ItemPedido(null, this, produto, quantidade);
        this.itens.add(novoItem);
    }

    public BigDecimal calcularValorTotal() {
        return itens.stream()
                .map(ItemPedido::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Pedido comId(Long novoId) {
        Pedido copia = new Pedido(novoId, this.cliente, this.dataPedido);
        this.itens.forEach(item -> copia.adicionarItem(item.getProduto(), item.getQuantidade()));
        return copia;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }
}
