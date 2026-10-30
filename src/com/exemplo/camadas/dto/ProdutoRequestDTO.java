package com.exemplo.camadas.dto;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record ProdutoRequestDTO(Long id, String nome, BigDecimal preco) {

    public static ProdutoRequestDTO criarAPartirDeFormularioUrlEncoded(String corpoFormularioBruto) {
        Map<String, String> camposDoFormulario = extrairMapaDeParametros(corpoFormularioBruto);
        Long identificador = converterTextoParaId(camposDoFormulario.get("id"));
        String nomeInformado = camposDoFormulario.getOrDefault("nome", "");
        BigDecimal precoInformado = converterTextoParaPreco(camposDoFormulario.get("preco"));
        return new ProdutoRequestDTO(identificador, nomeInformado, precoInformado);
    }

    public static Map<String, String> extrairMapaDeParametros(String conteudoUrlEncoded) {
        Map<String, String> mapaDeParametros = new HashMap<>();
        if (conteudoUrlEncoded == null || conteudoUrlEncoded.isBlank()) {
            return mapaDeParametros;
        }
        for (String parChaveValor : conteudoUrlEncoded.split("&")) {
            String[] partesDoParametro = parChaveValor.split("=", 2);
            String nomeDoCampo = URLDecoder.decode(partesDoParametro[0], StandardCharsets.UTF_8);
            String valorDoCampo = partesDoParametro.length > 1
                    ? URLDecoder.decode(partesDoParametro[1], StandardCharsets.UTF_8)
                    : "";
            mapaDeParametros.put(nomeDoCampo, valorDoCampo);
        }
        return mapaDeParametros;
    }

    public static Long converterTextoParaId(String textoIdentificador) {
        if (textoIdentificador == null || textoIdentificador.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(textoIdentificador.trim());
        } catch (NumberFormatException excecaoFormatoNumero) {
            throw new IllegalArgumentException("O identificador informado é inválido.");
        }
    }

    private static BigDecimal converterTextoParaPreco(String textoPreco) {
        if (textoPreco == null || textoPreco.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(textoPreco.trim().replace(",", "."));
        } catch (NumberFormatException excecaoFormatoNumero) {
            throw new IllegalArgumentException("O formato do preço informado é inválido.");
        }
    }
}
