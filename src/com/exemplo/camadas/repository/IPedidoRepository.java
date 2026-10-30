package com.exemplo.camadas.repository;

import com.exemplo.camadas.model.Pedido;
import java.util.Optional;

public interface IPedidoRepository {

    Pedido criarPedido(Pedido pedido);

    Optional<Pedido> carregarPedidoPorId(Long id);
}
