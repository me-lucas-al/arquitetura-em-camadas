package com.exemplo.camadas.valueobject;

import com.exemplo.camadas.exception.DomainValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Preco {

    private final BigDecimal valor;

    public Preco(BigDecimal valor) {
        validar(valor);
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    private void validar(BigDecimal candidato) {
        if (candidato == null) {
            throw new DomainValidationException("O preço do produto é obrigatório.");
        }
        if (candidato.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("O preço do produto não pode ser negativo.");
        }
        if (candidato.compareTo(BigDecimal.ZERO) == 0) {
            throw new DomainValidationException("O preço do produto deve ser maior que zero.");
        }
    }

    public BigDecimal getValor() {
        return valor;
    }
}
