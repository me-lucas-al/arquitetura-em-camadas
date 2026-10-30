package com.exemplo.camadas.repository;

import com.exemplo.camadas.model.Produto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProdutoRepository implements IProdutoRepository {

    private final Map<Long, Produto> armazenamentoEmMemoria = new ConcurrentSkipListMap<>();
    private final AtomicLong geradorSequencialDeId = new AtomicLong(1);

    @Override
    public Produto salvar(Produto produtoNovo) {
        Long novoIdentificador = geradorSequencialDeId.getAndIncrement();
        Produto produtoComIdentificador = produtoNovo.comId(novoIdentificador);
        armazenamentoEmMemoria.put(novoIdentificador, produtoComIdentificador);
        return produtoComIdentificador;
    }

    @Override
    public Produto atualizar(Produto produtoExistente) {
        armazenamentoEmMemoria.put(produtoExistente.getId(), produtoExistente);
        return produtoExistente;
    }

    @Override
    public boolean removerPorId(Long identificadorProduto) {
        return armazenamentoEmMemoria.remove(identificadorProduto) != null;
    }

    @Override
    public Optional<Produto> buscarPorId(Long identificadorProduto) {
        if (identificadorProduto == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(armazenamentoEmMemoria.get(identificadorProduto));
    }

    @Override
    public boolean existePorNomeIgnorandoId(String nomeBuscado, Long identificadorIgnorado) {
        return armazenamentoEmMemoria.values().stream()
                .filter(produtoCadastrado -> !Objects.equals(produtoCadastrado.getId(), identificadorIgnorado))
                .anyMatch(produtoCadastrado -> produtoCadastrado.getNome().getValor().equalsIgnoreCase(nomeBuscado));
    }

    @Override
    public List<Produto> listarTodos() {
        return List.copyOf(armazenamentoEmMemoria.values());
    }
}
