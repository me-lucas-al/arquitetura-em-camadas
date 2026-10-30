package com.exemplo.camadas.valueobject;

import com.exemplo.camadas.exception.DomainValidationException;

public final class NomeProduto {

    private static final int TAMANHO_MINIMO = 3;
    private static final int TAMANHO_MAXIMO = 120;

    private final String valor;

    public NomeProduto(String valor) {
        validar(valor);
        this.valor = valor.trim();
    }

    private void validar(String candidato) {
        if (candidato == null || candidato.isBlank()) {
            throw new DomainValidationException("O nome do produto não pode ser vazio.");
        }
        String normalizado = candidato.trim();
        if (normalizado.length() < TAMANHO_MINIMO) {
            throw new DomainValidationException("O nome do produto deve ter no mínimo 3 caracteres.");
        }
        if (normalizado.length() > TAMANHO_MAXIMO) {
            throw new DomainValidationException("O nome do produto não pode exceder 120 caracteres.");
        }
    }

    public String getValor() {
        return valor;
    }
}
