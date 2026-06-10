package org.aep.observacao.controller.dto;

import org.aep.observacao.model.Usuario;

public record UsuarioResponse(
        int id,
        String nome,
        String email,
        String telefone) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone());
    }
}