package org.aep.observacao.controller.dto;

import org.aep.observacao.model.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull Status status,
        @NotBlank String responsavel,
        @NotBlank String comentario) {
}