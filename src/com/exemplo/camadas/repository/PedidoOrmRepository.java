package com.exemplo.camadas.repository;

import com.exemplo.camadas.model.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Optional;

public class PedidoOrmRepository implements IPedidoRepository {

    private final EntityManager entityManager;

    public PedidoOrmRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Pedido criarPedido(Pedido pedido) {
        EntityTransaction transacao = entityManager.getTransaction();
        try {
            transacao.begin();
            entityManager.persist(pedido);
            transacao.commit();
            return pedido;
        } catch (RuntimeException erroDePersistencia) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw erroDePersistencia;
        }
    }

    @Override
    public Optional<Pedido> carregarPedidoPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Pedido.class, id));
    }
}
