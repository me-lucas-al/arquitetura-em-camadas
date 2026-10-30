package com.exemplo.camadas.service;

import com.exemplo.camadas.dto.ProdutoRequestDTO;
import com.exemplo.camadas.dto.ProdutoResponseDTO;
import java.util.List;

public interface IProdutoService {

    ProdutoResponseDTO cadastrar(ProdutoRequestDTO requestDTO);

    ProdutoResponseDTO atualizar(ProdutoRequestDTO requestDTO);

    void remover(Long id);

    ProdutoResponseDTO buscarPorId(Long id);

    List<ProdutoResponseDTO> listarTodos();
}
