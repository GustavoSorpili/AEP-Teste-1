package org.aep.observacao.controller.dto;

import org.aep.observacao.model.Solicitacao;

public record SolicitacaoResponse(
        int id,
        String protocolo,
        String categoria,
        String descricao,
        String localizacao,
        String prioridade,
        String status,
        String dataCriacao,
        boolean anonimo,
        UsuarioResponse usuario) {

    public static SolicitacaoResponse from(Solicitacao solicitacao) {
        UsuarioResponse usuarioResponse = solicitacao.getUsuario() == null ? null : UsuarioResponse.from(solicitacao.getUsuario());
        return new SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getProtocolo(),
                solicitacao.getCategoria().getNome(),
                solicitacao.getDescricao(),
                solicitacao.getLocalizacao(),
                solicitacao.getPrioridade().name(),
                solicitacao.getStatus().name(),
                solicitacao.getDataCriacao().toString(),
                solicitacao.isAnonimo(),
                usuarioResponse);
    }
}