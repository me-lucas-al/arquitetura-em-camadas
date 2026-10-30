package com.exemplo.camadas.repository;

import com.exemplo.camadas.model.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEntityManager implements EntityManager {

    private final Map<Long, Pedido> tabelaPedidos = new ConcurrentSkipListMap<>();
    private final AtomicLong geradorDeId = new AtomicLong(1);
    private boolean transacaoAtiva = false;

    @Override
    public void persist(Object entity) {
        if (entity instanceof Pedido pedido) {
            Long id = pedido.getId() != null ? pedido.getId() : geradorDeId.getAndIncrement();
            tabelaPedidos.put(id, pedido.comId(id));
        }
    }

    @Override
    public <T> T merge(T entity) {
        persist(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        if (entity instanceof Pedido pedido && pedido.getId() != null) {
            tabelaPedidos.remove(pedido.getId());
        }
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        if (entityClass.equals(Pedido.class) && primaryKey instanceof Long id) {
            return entityClass.cast(tabelaPedidos.get(id));
        }
        return null;
    }

    @Override
    public EntityTransaction getTransaction() {
        return new EntityTransaction() {
            @Override
            public void begin() {
                transacaoAtiva = true;
            }

            @Override
            public void commit() {
                transacaoAtiva = false;
            }

            @Override
            public void rollback() {
                transacaoAtiva = false;
            }

            @Override
            public boolean isActive() {
                return transacaoAtiva;
            }
        };
    }

    @Override
    public void close() {
        transacaoAtiva = false;
    }
}
