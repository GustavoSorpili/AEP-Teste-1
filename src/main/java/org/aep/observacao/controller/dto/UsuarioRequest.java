package org.aep.observacao.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
        Integer id,
        @NotBlank String nome,
        @NotBlank String email,
        @NotBlank String telefone) {
}