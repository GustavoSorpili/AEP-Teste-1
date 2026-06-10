package org.aep.observacao.controller.dto;

import org.aep.observacao.model.Prioridade;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarSolicitacaoRequest(
        @NotBlank String categoriaNome,
        @Min(1) int categoriaSlaDias,
        @NotBlank String descricao,
        @NotBlank String localizacao,
        @NotNull Prioridade prioridade,
        @Valid UsuarioRequest usuario,
        boolean anonimo) {
}