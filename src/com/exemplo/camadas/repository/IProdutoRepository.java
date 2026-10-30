package com.exemplo.camadas.repository;

import com.exemplo.camadas.model.Produto;
import java.util.List;
import java.util.Optional;

public interface IProdutoRepository {

    Produto salvar(Produto produto);

    Produto atualizar(Produto produto);

    boolean removerPorId(Long id);

    Optional<Produto> buscarPorId(Long id);

    boolean existePorNomeIgnorandoId(String nome, Long idIgnorado);

    List<Produto> listarTodos();
}
