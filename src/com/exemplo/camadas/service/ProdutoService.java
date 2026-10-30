package com.exemplo.camadas.service;

import com.exemplo.camadas.dto.ProdutoRequestDTO;
import com.exemplo.camadas.dto.ProdutoResponseDTO;
import com.exemplo.camadas.exception.DomainValidationException;
import com.exemplo.camadas.mapper.ProdutoMapper;
import com.exemplo.camadas.model.Produto;
import com.exemplo.camadas.repository.IProdutoRepository;
import java.util.List;

public class ProdutoService implements IProdutoService {

    private final IProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoService(IProdutoRepository produtoRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
    }

    @Override
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dadosRequisicao) {
        Produto novoProduto = produtoMapper.converterParaEntidadeProduto(dadosRequisicao);
        validarUnicidadeDoNome(novoProduto.getNome().getValor(), null);
        Produto produtoSalvo = produtoRepository.salvar(novoProduto);
        return produtoMapper.converterParaProdutoResponseDTO(produtoSalvo);
    }

    @Override
    public ProdutoResponseDTO atualizar(ProdutoRequestDTO dadosRequisicao) {
        buscarProdutoExistenteOuFalhar(dadosRequisicao.id());
        Produto produtoAtualizado = produtoMapper.converterParaEntidadeProduto(dadosRequisicao);
        validarUnicidadeDoNome(produtoAtualizado.getNome().getValor(), dadosRequisicao.id());
        Produto produtoAtualizadoSalvo = produtoRepository.atualizar(produtoAtualizado);
        return produtoMapper.converterParaProdutoResponseDTO(produtoAtualizadoSalvo);
    }

    @Override
    public void remover(Long identificadorProduto) {
        buscarProdutoExistenteOuFalhar(identificadorProduto);
        produtoRepository.removerPorId(identificadorProduto);
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long identificadorProduto) {
        Produto produtoEncontrado = buscarProdutoExistenteOuFalhar(identificadorProduto);
        return produtoMapper.converterParaProdutoResponseDTO(produtoEncontrado);
    }

    @Override
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.listarTodos()
                .stream()
                .map(produtoMapper::converterParaProdutoResponseDTO)
                .toList();
    }

    private Produto buscarProdutoExistenteOuFalhar(Long identificadorProduto) {
        if (identificadorProduto == null) {
            throw new DomainValidationException("O ID do produto é obrigatório para esta operação.");
        }
        return produtoRepository.buscarPorId(identificadorProduto)
                .orElseThrow(() -> new DomainValidationException("Produto não encontrado para o ID informado."));
    }

    private void validarUnicidadeDoNome(String nomeDoProduto, Long identificadorIgnorado) {
        if (produtoRepository.existePorNomeIgnorandoId(nomeDoProduto, identificadorIgnorado)) {
            throw new DomainValidationException("Já existe um produto cadastrado com o nome '" + nomeDoProduto + "'.");
        }
    }
}
